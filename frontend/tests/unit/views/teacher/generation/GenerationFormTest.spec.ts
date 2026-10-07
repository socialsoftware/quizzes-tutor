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
import TopicNode from '@/models/management/TopicNode';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import GenerationForm from '@/views/teacher/generation/GenerationForm.vue';

const topicNode = (id: number, name: string, parentId: number | null = null, sources: { materialId: string; sectionPath: string }[] = []) =>
  new TopicNode({ id, name, parentId, sequence: null, numberOfQuestions: 0, sources } as TopicNode);

const material = (id: string, status: string) =>
  new GenerationMaterial({ id, filename: `${id}.pdf`, status, chunkCount: 3, parser: 'x', parseSeconds: 1, error: null } as GenerationMaterial);

describe('GenerationForm', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue([
      topicNode(4, 'Networks'),
      topicNode(5, 'Chapter', null, [{ materialId: 'm1', sectionPath: 'Chapter' }]),
      topicNode(6, 'Section', 5, [{ materialId: 'm1', sectionPath: 'Chapter > Section' }, { materialId: 'm2', sectionPath: 'Intro' }]),
    ]);
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
      fromTopic: false,
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
    (wrapper.vm as any).selectedTopic = topicNode(4, 'Networks');
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

  test('all the sections ticked ask for the same as none, so no list is sent', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';

    await wrapper.find('[data-cy="SelectAllSections"]').trigger('click');
    expect((wrapper.vm as any).selectedSections).toEqual(['m1 > Intro', 'm1 > Intro > Rules']);

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(expect.objectContaining({ sections: [] }));
  });

  test('a partial choice of sections is sent as it is, and Clear empties it', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = 'HTTP';
    (wrapper.vm as any).selectedSections = ['m1 > Intro'];
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();
    expect(request).toHaveBeenLastCalledWith(expect.objectContaining({ sections: ['m1 > Intro'] }));

    await wrapper.find('[data-cy="ClearSections"]').trigger('click');
    expect((wrapper.vm as any).selectedSections).toEqual([]);
  });

  test('Select all and Clear are off when there is nothing to select or clear', async () => {
    const wrapper = await mountForm([material('m1', 'PROCESSING')]);

    expect(wrapper.find('[data-cy="SelectAllSections"]').attributes('disabled')).toBeDefined();
    expect(wrapper.find('[data-cy="ClearSections"]').attributes('disabled')).toBeDefined();
  });

  test('a topic without document sections is read from the materials picked by hand', async () => {
    const wrapper = await mountForm([material('m1', 'READY')]);

    (wrapper.vm as any).selectedTopic = topicNode(4, 'Networks');
    await flushPromises();

    expect((wrapper.vm as any).sourceMode).toBe('manual');
    expect(wrapper.find('[data-cy="GenerationSourceMode"]').exists()).toBe(false);
  });

  test('a topic with document sections is read from them by default, with its subtopics', async () => {
    const wrapper = await mountForm([material('m1', 'READY')]);

    (wrapper.vm as any).selectedTopic = (wrapper.vm as any).topics.find((topic: TopicNode) => topic.id === 5);
    await flushPromises();

    expect((wrapper.vm as any).sourceMode).toBe('topic');
    expect((wrapper.vm as any).topicSections.map((source: any) => source.sectionPath)).toEqual([
      'Chapter', 'Chapter > Section', 'Intro',
    ]);
    expect((wrapper.vm as any).topicSectionsLabel).toBe("Use the topic's sections (3 sections from 2 documents)");
    expect(wrapper.find('[data-cy="GenerationMaterials"]').exists()).toBe(false);
  });

  test('asking for the topic sections sends only the topic, not materials or sections', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedSections = ['m1 > Intro'];
    (wrapper.vm as any).selectedTopic = (wrapper.vm as any).topics.find((topic: TopicNode) => topic.id === 5);
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(
      expect.objectContaining({ topicId: 5, topic: 'Chapter', fromTopic: true, materialIds: [], sections: [] })
    );
  });

  test('the topic sections need no material to be ready', async () => {
    const wrapper = await mountForm([material('m1', 'PROCESSING')]);
    (wrapper.vm as any).selectedTopic = (wrapper.vm as any).topics.find((topic: TopicNode) => topic.id === 5);
    await flushPromises();

    expect(generateButton(wrapper).attributes('disabled')).toBeUndefined();
  });

  test('the teacher can still choose the materials by hand for a topic with sections', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = (wrapper.vm as any).topics.find((topic: TopicNode) => topic.id === 5);
    await flushPromises();

    (wrapper.vm as any).sourceMode = 'manual';
    await flushPromises();
    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(
      expect.objectContaining({ topicId: 5, fromTopic: false, materialIds: ['m1'] })
    );
  });

  test('topics created from a document are offered, and the picked topic keeps up with them', async () => {
    const tree = vi.spyOn(RemoteServices, 'getTopicTree');
    const wrapper = await mountForm([material('m1', 'READY')]);
    (wrapper.vm as any).selectedTopic = (wrapper.vm as any).topics.find((topic: TopicNode) => topic.id === 4);
    expect((wrapper.vm as any).topicSections).toEqual([]);

    tree.mockResolvedValue([
      topicNode(4, 'Networks', null, [{ materialId: 'm1', sectionPath: 'Networks > HTTP' }]),
    ]);
    await wrapper.setProps({ topicsVersion: 1 });
    await flushPromises();

    expect((wrapper.vm as any).topics.map((topic: TopicNode) => topic.name)).toEqual(['Networks']);
    expect((wrapper.vm as any).selectedTopic.id).toBe(4);
    expect((wrapper.vm as any).sourceMode).toBe('topic');
  });
});
