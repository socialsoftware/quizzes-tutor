import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import GenerationJob from '@/models/management/generation/GenerationJob';
import TopicNode from '@/models/management/TopicNode';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import GenerationForm from '@/views/teacher/generation/GenerationForm.vue';

const topicNode = (id: number, name: string, parentId: number | null = null, chunkIds: string[] = []) =>
  new TopicNode({
    id,
    name,
    parentId,
    sequence: null,
    numberOfQuestions: 0,
    sources: chunkIds.map((chunkId) => ({ materialId: chunkId.split(':')[0], chunkId })),
  } as TopicNode);

describe('GenerationForm', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue([
      topicNode(4, 'Empty'),
      topicNode(5, 'Chapter', null, ['m1:0']),
      topicNode(6, 'Section', 5, ['m1:1', 'm2:0']),
    ]);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  const mountForm = async () => {
    const wrapper = mount(GenerationForm, {
      props: { topicsVersion: 0 },
      global: { plugins: [createVuetify({ components, directives })] },
    });
    await flushPromises();
    return wrapper;
  };

  const vm = (wrapper: any) => wrapper.vm as any;
  const generateButton = (wrapper: any) => wrapper.find('[data-cy="GenerateButton"]');

  test('offers the topics in tree order with how much text is under each, subtopics included', async () => {
    const wrapper = await mountForm();

    expect(vm(wrapper).topicItems.map((item: any) => [item.title, item.depth, item.pieces, item.documents])).toEqual([
      ['Empty', 0, 0, 0],
      ['Chapter', 0, 3, 2],
      ['Section', 1, 2, 2],
    ]);
  });

  test('cannot generate without a topic, nor about a topic with no text under it', async () => {
    const wrapper = await mountForm();
    expect(generateButton(wrapper).attributes('disabled')).toBeDefined();

    vm(wrapper).topicId = 4;
    await flushPromises();
    expect(generateButton(wrapper).attributes('disabled')).toBeDefined();
    expect(vm(wrapper).topicItemProps(vm(wrapper).topicItems[0])).toEqual({ disabled: true });

    vm(wrapper).topicId = 5;
    await flushPromises();
    expect(generateButton(wrapper).attributes('disabled')).toBeUndefined();
    expect(vm(wrapper).topicHint).toBe('Written from 3 piece(s) of 2 document(s)');
  });

  test('rejects a number of questions outside 1 to 20', async () => {
    const wrapper = await mountForm();
    vm(wrapper).topicId = 5;

    for (const count of [0, 21, 2.5]) {
      vm(wrapper).count = count;
      await flushPromises();
      expect(generateButton(wrapper).attributes('disabled')).toBeDefined();
    }
  });

  test('a focus over 500 characters blocks the request', async () => {
    const wrapper = await mountForm();
    vm(wrapper).topicId = 5;
    vm(wrapper).focus = 'x'.repeat(501);
    await flushPromises();

    expect(generateButton(wrapper).attributes('disabled')).toBeDefined();
  });

  test('sends the topic and the options, and tells the page about the job', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm();
    vm(wrapper).topicId = 6;
    vm(wrapper).difficulty = 'HARD';
    vm(wrapper).count = 3;
    vm(wrapper).groundingMode = 'ENRICHED';
    vm(wrapper).focus = '  name servers ';
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith({
      topicId: 6,
      difficulty: 'HARD',
      count: 3,
      groundingMode: 'ENRICHED',
      language: null,
      focus: 'name servers',
    });
    expect(wrapper.emitted('requested')).toHaveLength(1);
  });

  test('sends the chosen language, or none to keep the language of the materials', async () => {
    const request = vi.spyOn(RemoteServices, 'requestGeneration').mockResolvedValue(new GenerationJob());
    const wrapper = await mountForm();
    vm(wrapper).topicId = 5;
    vm(wrapper).language = 'Portuguese';
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(request).toHaveBeenCalledWith(expect.objectContaining({ language: 'Portuguese' }));
  });

  test('a refused request is reported and nothing is emitted', async () => {
    vi.spyOn(RemoteServices, 'requestGeneration').mockRejectedValue('The question generation service is not available');
    const wrapper = await mountForm();
    vm(wrapper).topicId = 5;
    await flushPromises();

    await generateButton(wrapper).trigger('click');
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('The question generation service is not available');
    expect(wrapper.emitted('requested')).toBeUndefined();
  });

  test('topics are read again when a document was put under them, and a topic that is gone is unpicked', async () => {
    const tree = vi.spyOn(RemoteServices, 'getTopicTree');
    const wrapper = await mountForm();
    vm(wrapper).topicId = 6;

    tree.mockResolvedValue([topicNode(4, 'Empty', null, ['m3:0', 'm3:1'])]);
    await wrapper.setProps({ topicsVersion: 1 });
    await flushPromises();

    expect(vm(wrapper).topicItems.map((item: any) => [item.title, item.pieces])).toEqual([['Empty', 2]]);
    expect(vm(wrapper).topicId).toBeNull();
  });
});
