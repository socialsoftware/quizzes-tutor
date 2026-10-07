import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import OutlineNode, { OutlineEditResult } from '@/models/management/generation/OutlineNode';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import OutlineEditorDialog from '@/views/teacher/generation/OutlineEditorDialog.vue';

const node = (path: string, depth: number, extra: Record<string, unknown> = {}) =>
  new OutlineNode({
    path,
    title: path.split(' > ').pop()!,
    depth,
    has_text: true,
    paragraph_count: 3,
    preview: `text of ${path}`,
    ...extra,
  });

const outline = () => [
  node('Networks', 1, { has_text: false, paragraph_count: 0, preview: '' }),
  node('Networks > HTTP', 2),
  node('Networks > DNS', 2, { paragraph_count: 1 }),
  node('Databases', 1),
];

describe('OutlineEditorDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getGenerationOutline').mockResolvedValue(outline());
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async () => {
    const wrapper = mount(OutlineEditorDialog, {
      props: { dialog: true, materialId: 'm1', materialName: 'book.pdf' },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  const vm = (wrapper: any) => wrapper.vm as any;
  const reply = (nodes: OutlineNode[]) => {
    const result = new OutlineEditResult({ outline: [], path_map: {} });
    result.outline = nodes;
    return result;
  };
  const rows = () => Array.from(document.body.querySelectorAll('[data-cy="OutlineNode"]'));
  const icon = (row: Element, cy: string) => row.querySelector(`[data-cy="${cy}"]`) as HTMLElement;

  test('lists the headings of the document, marking those without text', async () => {
    await mountDialog();

    expect(RemoteServices.getGenerationOutline).toHaveBeenCalledWith('m1');
    expect(document.body.textContent).toContain('Sections of "book.pdf"');
    expect(rows()).toHaveLength(4);
    expect(rows()[0].textContent).toContain('heading only');
    expect(rows()[1].textContent).not.toContain('heading only');
    expect(rows()[1].textContent).toContain('text of Networks > HTTP');
  });

  test('says so when the document has no sections', async () => {
    (RemoteServices.getGenerationOutline as any).mockResolvedValue([]);

    await mountDialog();

    expect(document.body.querySelector('[data-cy="NoOutline"]')).not.toBeNull();
  });

  test('only offers the changes that can be made to each section', async () => {
    await mountDialog();

    const disabled = (row: Element, cy: string) => icon(row, cy).classList.contains('v-icon--disabled');
    // the first one has nothing before it to join, and is already at the top
    expect(disabled(rows()[0], 'MergeSection')).toBe(true);
    expect(disabled(rows()[0], 'PromoteSection')).toBe(true);
    // HTTP is the first of its level: nothing above it to go under
    expect(disabled(rows()[1], 'DemoteSection')).toBe(true);
    expect(disabled(rows()[1], 'PromoteSection')).toBe(false);
    // DNS can go under HTTP, but has a single paragraph so there is nothing to cut
    expect(disabled(rows()[2], 'DemoteSection')).toBe(false);
    expect(disabled(rows()[2], 'SplitSection')).toBe(true);
    // Databases has Networks before it at the same level
    expect(disabled(rows()[3], 'DemoteSection')).toBe(false);
    expect(disabled(rows()[3], 'MergeSection')).toBe(false);
  });

  test('a section cannot go under a sibling that belongs to another parent', async () => {
    // D is the first section of C: B is at the same level but under A, so D cannot join it
    (RemoteServices.getGenerationOutline as any).mockResolvedValue([
      node('A', 1), node('A > B', 2), node('C', 1), node('C > D', 2),
    ]);
    const wrapper = await mountDialog();

    expect(vm(wrapper).canDemote(3)).toBe(false);
    expect(vm(wrapper).canDemote(2)).toBe(true);
  });

  test('a section is not offered to go deeper than markdown allows', async () => {
    const deep = ['A', 'B', 'C', 'D', 'E', 'F'].map((_, i, all) => node(all.slice(0, i + 1).join(' > '), i + 1));
    deep.push(node('A > B > C > D > E > G', 6));
    (RemoteServices.getGenerationOutline as any).mockResolvedValue(deep);
    const wrapper = await mountDialog();

    expect(vm(wrapper).canDemote(6)).toBe(false);
  });

  test('renames a section and shows what the service answers', async () => {
    const edit = vi.spyOn(RemoteServices, 'editGenerationOutline').mockResolvedValue(
      reply([node('Networks', 1), node('Web', 2)])
    );
    const wrapper = await mountDialog();

    vm(wrapper).askRename(vm(wrapper).outline[1]);
    vm(wrapper).name = '  Web  ';
    await vm(wrapper).saveRename();
    await flushPromises();

    expect(edit).toHaveBeenCalledWith('m1', { op: 'rename', path: 'Networks > HTTP', title: 'Web' });
    expect(rows()).toHaveLength(2);
    expect(wrapper.emitted('edited')).toHaveLength(1);
  });

  test('a refused edit is shown and the outline stays as it was', async () => {
    vi.spyOn(RemoteServices, 'editGenerationOutline').mockRejectedValue(new Error('a section with that name already exists here'));
    const wrapper = await mountDialog();

    vm(wrapper).askRename(vm(wrapper).outline[1]);
    vm(wrapper).name = 'DNS';
    await vm(wrapper).saveRename();
    await flushPromises();

    expect(store.setError).toHaveBeenCalled();
    expect(rows()).toHaveLength(4);
    expect(wrapper.emitted('edited')).toBeUndefined();
  });

  test('joining a section asks first, and does nothing if the teacher declines', async () => {
    const edit = vi.spyOn(RemoteServices, 'editGenerationOutline').mockResolvedValue(reply([node('Networks', 1)]));
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountDialog();

    await vm(wrapper).merge(vm(wrapper).outline[2]);
    expect(edit).not.toHaveBeenCalled();

    confirmation.mockReturnValue(true);
    await vm(wrapper).merge(vm(wrapper).outline[2]);
    await flushPromises();
    expect(edit).toHaveBeenCalledWith('m1', { op: 'merge', path: 'Networks > DNS' });
  });

  test('moves a section up or down a level', async () => {
    const edit = vi.spyOn(RemoteServices, 'editGenerationOutline').mockResolvedValue(reply(outline()));
    const wrapper = await mountDialog();

    await vm(wrapper).shift(vm(wrapper).outline[2], -1);
    await vm(wrapper).shift(vm(wrapper).outline[3], 1);

    expect(edit).toHaveBeenNthCalledWith(1, 'm1', { op: 'shift', path: 'Networks > DNS', delta: -1 });
    expect(edit).toHaveBeenNthCalledWith(2, 'm1', { op: 'shift', path: 'Databases', delta: 1 });
  });

  test('cuts a section at the paragraph the teacher picks', async () => {
    vi.spyOn(RemoteServices, 'getGenerationSectionText').mockResolvedValue([
      { index: 0, text: 'First.' },
      { index: 1, text: 'Second.' },
      { index: 2, text: 'Third.' },
    ]);
    const edit = vi.spyOn(RemoteServices, 'editGenerationOutline').mockResolvedValue(reply(outline()));
    const wrapper = await mountDialog();

    await vm(wrapper).askSplit(vm(wrapper).outline[1]);
    await flushPromises();
    const paragraphs = Array.from(document.body.querySelectorAll('[data-cy="Paragraph"]')) as HTMLElement[];
    expect(paragraphs).toHaveLength(3);

    // the first paragraph cannot start a new section: there would be nothing left in the old one
    paragraphs[0].click();
    await flushPromises();
    expect(vm(wrapper).startAt).toBeNull();

    paragraphs[2].click();
    vm(wrapper).name = 'Cookies';
    await flushPromises();
    await vm(wrapper).saveSplit();
    await flushPromises();

    expect(RemoteServices.getGenerationSectionText).toHaveBeenCalledWith('m1', 'Networks > HTTP');
    expect(edit).toHaveBeenCalledWith('m1', { op: 'split', path: 'Networks > HTTP', title: 'Cookies', paragraph: 2 });
  });

  test('a cut needs a paragraph and a name', async () => {
    vi.spyOn(RemoteServices, 'getGenerationSectionText').mockResolvedValue([
      { index: 0, text: 'First.' },
      { index: 1, text: 'Second.' },
    ]);
    const edit = vi.spyOn(RemoteServices, 'editGenerationOutline').mockResolvedValue(reply(outline()));
    const wrapper = await mountDialog();
    await vm(wrapper).askSplit(vm(wrapper).outline[1]);

    await vm(wrapper).saveSplit();
    vm(wrapper).startAt = 1;
    await vm(wrapper).saveSplit();

    expect(edit).not.toHaveBeenCalled();
  });
});
