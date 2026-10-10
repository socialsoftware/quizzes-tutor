import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import MaterialsPanel from '@/views/teacher/generation/MaterialsPanel.vue';

const material = (overrides: Partial<GenerationMaterial>) =>
  new GenerationMaterial({
    id: 'm1',
    filename: 'lecture.pdf',
    status: 'READY',
    chunkCount: 12,
    parser: 'pymupdf4llm',
    parseSeconds: 1.5,
    error: null,
    ...overrides,
  } as GenerationMaterial);

describe('MaterialsPanel', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.useRealTimers();
  });

  const mountPanel = async () => {
    const wrapper = mount(MaterialsPanel, {
      global: { plugins: [createVuetify({ components, directives })], stubs: { DistributeMaterialDialog: true } },
    });
    await flushPromises();
    return wrapper;
  };

  const dialogOf = (wrapper: any) => wrapper.findComponent({ name: 'DistributeMaterialDialog' });

  test('lists the materials with their state and tells the parent about them', async () => {
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([
      material({}),
      material({ id: 'm2', filename: 'broken.pptx', status: 'FAILED', error: 'corrupt file' }),
    ]);

    const wrapper = await mountPanel();

    const rows = wrapper.findAll('tbody tr');
    expect(rows).toHaveLength(2);
    expect(rows[0].text()).toContain('lecture.pdf');
    expect(rows[0].text()).toContain('READY');
    expect(rows[1].text()).toContain('FAILED');
    expect((wrapper.emitted('update:materials')![0][0] as GenerationMaterial[]).map((m) => m.id)).toEqual(['m1', 'm2']);
  });

  test('says so when there is nothing yet', async () => {
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([]);

    const wrapper = await mountPanel();

    expect(wrapper.text()).toContain('No material yet');
  });

  test('uploads every chosen file and reloads the list', async () => {
    const list = vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([]);
    const upload = vi.spyOn(RemoteServices, 'uploadGenerationMaterial').mockResolvedValue(material({}));
    const wrapper = await mountPanel();
    const input = wrapper.find('[data-cy="MaterialFile"]');
    const files = [new File(['a'], 'a.pdf'), new File(['b'], 'b.md')];
    Object.defineProperty(input.element, 'files', { value: files, configurable: true });

    await input.trigger('change');
    await flushPromises();

    expect(upload).toHaveBeenCalledTimes(2);
    expect(upload.mock.calls.map((call) => call[0].name)).toEqual(['a.pdf', 'b.md']);
    expect(list.mock.calls.length).toBeGreaterThan(1);
  });

  test('refuses a file over the size the server accepts', async () => {
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([]);
    const upload = vi.spyOn(RemoteServices, 'uploadGenerationMaterial');
    const wrapper = await mountPanel();
    const input = wrapper.find('[data-cy="MaterialFile"]');
    const huge = new File(['x'], 'huge.pdf');
    Object.defineProperty(huge, 'size', { value: 101 * 1024 * 1024 });
    Object.defineProperty(input.element, 'files', { value: [huge], configurable: true });

    await input.trigger('change');
    await flushPromises();

    expect(upload).not.toHaveBeenCalled();
    expect(store.setError).toHaveBeenCalledWith(expect.stringContaining('huge.pdf'));
  });

  test('keeps looking while a material is being processed, then stops', async () => {
    vi.useFakeTimers();
    const list = vi
      .spyOn(RemoteServices, 'getGenerationMaterials')
      .mockResolvedValueOnce([material({ status: 'PROCESSING', chunkCount: 0, parser: null })])
      .mockResolvedValue([material({})]);

    mount(MaterialsPanel, {
      global: { plugins: [createVuetify({ components, directives })], stubs: { DistributeMaterialDialog: true } },
    });
    await vi.advanceTimersByTimeAsync(0);
    expect(list).toHaveBeenCalledTimes(1);

    await vi.advanceTimersByTimeAsync(3000);
    expect(list).toHaveBeenCalledTimes(2);

    await vi.advanceTimersByTimeAsync(30_000);
    expect(list).toHaveBeenCalledTimes(2);
  });

  test('a failed upload is reported and the list is reloaded', async () => {
    const list = vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([]);
    vi.spyOn(RemoteServices, 'uploadGenerationMaterial').mockRejectedValue('Unable to connect to server');
    const wrapper = await mountPanel();
    const input = wrapper.find('[data-cy="MaterialFile"]');
    Object.defineProperty(input.element, 'files', { value: [new File(['a'], 'a.pdf')], configurable: true });

    await input.trigger('change');
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('Unable to connect to server');
    expect(list.mock.calls.length).toBeGreaterThan(1);
  });

  test('a finished document can be put under topics, and the page hears when that is saved', async () => {
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([
      material({ id: 'ready', filename: 'book.pdf', placedChunks: 4 }),
      material({ id: 'busy', filename: 'later.pdf', status: 'PROCESSING', chunkCount: 0, parser: null }),
    ]);
    const wrapper = await mountPanel();

    expect(wrapper.findAll('[data-cy="DistributeMaterial"]')).toHaveLength(1);
    expect(dialogOf(wrapper).exists()).toBe(false);

    await wrapper.find('[data-cy="DistributeMaterial"]').trigger('click');
    const dialog = dialogOf(wrapper);

    expect(dialog.props('materialId')).toBe('ready');
    expect(dialog.props('materialName')).toBe('book.pdf');

    dialog.vm.$emit('saved');
    await flushPromises();
    expect(wrapper.emitted('topics-changed')).toHaveLength(1);
  });

  test('shows how many pieces of each document are under a topic', async () => {
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([
      material({ id: 'done', placedChunks: 3 }),
      material({ id: 'new', placedChunks: 0 }),
    ]);
    const wrapper = await mountPanel();

    const chips = wrapper.findAll('[data-cy="PlacedChunks"]');
    expect(chips.map((chip) => chip.text())).toEqual(['3 / 12', '0 / 12']);
    expect(chips[1].classes().join(' ')).toContain('orange');
  });

  test('a document that finishes being read opens to be put under topics', async () => {
    vi.useFakeTimers();
    vi.spyOn(RemoteServices, 'getGenerationMaterials')
      .mockResolvedValueOnce([material({ status: 'PROCESSING', chunkCount: 0, parser: null })])
      .mockResolvedValue([material({ placedChunks: 0 })]);
    const wrapper = mount(MaterialsPanel, {
      global: { plugins: [createVuetify({ components, directives })], stubs: { DistributeMaterialDialog: true } },
    });
    await vi.advanceTimersByTimeAsync(0);
    expect(dialogOf(wrapper).exists()).toBe(false);

    await vi.advanceTimersByTimeAsync(3000);

    expect(dialogOf(wrapper).props('materialId')).toBe('m1');
  });

  test('a document read again that already has pieces under topics does not open by itself', async () => {
    vi.useFakeTimers();
    vi.spyOn(RemoteServices, 'getGenerationMaterials')
      .mockResolvedValueOnce([material({ status: 'PROCESSING', chunkCount: 0, parser: null })])
      .mockResolvedValue([material({ placedChunks: 5 })]);
    const wrapper = mount(MaterialsPanel, {
      global: { plugins: [createVuetify({ components, directives })], stubs: { DistributeMaterialDialog: true } },
    });
    await vi.advanceTimersByTimeAsync(3000);

    expect(dialogOf(wrapper).exists()).toBe(false);
  });

  test('reading a document again asks first, because its pieces leave their topics', async () => {
    vi.spyOn(RemoteServices, 'getGenerationMaterials').mockResolvedValue([material({})]);
    const reprocess = vi.spyOn(RemoteServices, 'reprocessGenerationMaterial').mockResolvedValue(material({}));
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountPanel();

    await wrapper.find('[data-cy="ReprocessMaterial"]').trigger('click');
    await flushPromises();
    expect(confirmation.mock.calls[0][0]).toContain('lecture.pdf');
    expect(confirmation.mock.calls[0][0]).toContain('leave their topics');
    expect(reprocess).not.toHaveBeenCalled();

    confirmation.mockReturnValue(true);
    await wrapper.find('[data-cy="ReprocessMaterial"]').trigger('click');
    await flushPromises();
    expect(reprocess).toHaveBeenCalledWith('m1');
  });
});
