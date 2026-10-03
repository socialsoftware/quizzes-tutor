import { flushPromises, mount } from '@vue/test-utils';
import { beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import GenerationJob from '@/models/management/generation/GenerationJob';
import GenerationSection from '@/models/management/generation/GenerationSection';
import Topic from '@/models/management/Topic';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import GenerationForm from '@/views/teacher/generation/GenerationForm.vue';

const material = (id: string, status: string) =>
  new GenerationMaterial({ id, filename: `${id}.pdf`, status, chunkCount: 3, parser: 'x', parseSeconds: 1, error: null } as GenerationMaterial);

describe('GenerationForm', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getTopics').mockResolvedValue([new Topic({ id: 4, name: 'Networks' } as Topic)]);
    vi.spyOn(RemoteServices, 'getGenerationSections').mockImplementation(async (materialId: string) => [
      new GenerationSection({ path: `${materialId} > Intro`, title: 'Intro', chunk_count: 1 }),
      new GenerationSection({ path: `${materialId} > Intro > Rules`, title: 'Rules', chunk_count: 2 }),
    ]);
  });

  const mountForm = async (materials: GenerationMaterial[]) => {
    const wrapper = mount(GenerationForm, {
      props: { materials },
      global: { plugins: [createVuetify({ components, directives })] },
    });
    await flushPromises();
    return wrapper;
  };

  const generateButton = (wrapper: any) => wrapper.find('[data-cy="GenerateButton"]');

  test('cannot generate without a topic', async () => {
    const wrapper = await mountForm([material('m1', 'READY')]);

    expect(generateButton(wrapper).attributes('disabled')).toBeDefined();

    (wrapper.vm as any).selectedTopic = 'HTTP';
    await flushPromises();

    expect(generateButton(wrapper).attributes('disabled')).toBeUndefined();
  });

  test('cannot generate before a material is ready', async () => {
    const wrapper = await mountForm([material('m1', 'PROCESSING')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';
    await flushPromises();

    expect(generateButton(wrapper).attributes('disabled')).toBeDefined();
  });

  test('starts with every ready material selected and ignores the ones still processing', async () => {
    const wrapper = await mountForm([material('m1', 'READY'), material('m2', 'PROCESSING'), material('m3', 'READY')]);

    expect((wrapper.vm as any).selectedMaterialIds).toEqual(['m1', 'm3']);
  });

  test('a material that becomes ready is offered without undoing the teacher choice', async () => {
    const wrapper = await mountForm([material('m1', 'READY'), material('m2', 'PROCESSING')]);
    (wrapper.vm as any).selectedMaterialIds = [];

    await wrapper.setProps({ materials: [material('m1', 'READY'), material('m2', 'READY')] });

    expect((wrapper.vm as any).selectedMaterialIds).toEqual([]);
  });

  test('rejects a number of questions outside 1 to 20', async () => {
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';

    for (const count of [0, 21, 2.5]) {
      (wrapper.vm as any).count = count;
      await flushPromises();
      expect(generateButton(wrapper).attributes('disabled')).toBeDefined();
    }
  });

  test('sends a topic typed by hand without a topic id', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = '  HTTP status codes ';
    (wrapper.vm as any).difficulty = 'HARD';
    (wrapper.vm as any).count = 3;
    (wrapper.vm as any).groundingMode = 'ENRICHED';
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith({
      topicId: null,
      topic: 'HTTP status codes',
      difficulty: 'HARD',
      count: 3,
      groundingMode: 'ENRICHED',
      materialIds: ['m1'],
      language: null,
      sections: [],
      focus: null,
    });
    expect(wrapper.emitted('requested')).toHaveLength(1);
  });

  test('offers the sections of the selected materials and sends the chosen ones with the focus', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY'), material('m2', 'READY')]);

    expect((wrapper.vm as any).sections.map((s: GenerationSection) => s.path)).toEqual([
      'm1 > Intro', 'm1 > Intro > Rules', 'm2 > Intro', 'm2 > Intro > Rules',
    ]);
    expect((wrapper.vm as any).sections[1].parentPath).toBe('m1 > Intro');

    (wrapper.vm as any).selectedTopic = 'HTTP';
    (wrapper.vm as any).selectedSections = ['m1 > Intro > Rules', 'm2 > Intro'];
    (wrapper.vm as any).focus = '  sign rules ';
    await flushPromises();
    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(
      expect.objectContaining({ sections: ['m1 > Intro > Rules', 'm2 > Intro'], focus: 'sign rules' })
    );
  });

  test('unselecting a material drops its sections from the choice', async () => {
    const wrapper = await mountForm([material('m1', 'READY'), material('m2', 'READY')]);
    (wrapper.vm as any).selectedSections = ['m1 > Intro', 'm2 > Intro'];

    (wrapper.vm as any).selectedMaterialIds = ['m2'];
    await flushPromises();

    expect((wrapper.vm as any).selectedSections).toEqual(['m2 > Intro']);
  });

  test('a focus over 500 characters blocks the request', async () => {
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';
    (wrapper.vm as any).focus = 'x'.repeat(501);
    await flushPromises();

    expect(generateButton(wrapper).attributes('disabled')).toBeDefined();
  });

  test('sends the chosen language, or none to keep the language of the materials', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';
    (wrapper.vm as any).language = 'Portuguese';
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(expect.objectContaining({ language: 'Portuguese' }));
  });

  test('links a topic picked from the course to its id', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = new Topic({ id: 4, name: 'Networks' } as Topic);
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(expect.objectContaining({ topicId: 4, topic: 'Networks' }));
  });

  test('a refused request is reported and nothing is emitted', async () => {
    vi.spyOn(RemoteServices, 'requestGeneration').mockRejectedValue('The question generation service is not available');
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('The question generation service is not available');
    expect(wrapper.emitted('requested')).toBeUndefined();
  });
});
