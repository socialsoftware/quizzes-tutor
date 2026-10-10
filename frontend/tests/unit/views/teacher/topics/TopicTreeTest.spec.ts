import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import Topic from '@/models/management/Topic';
import TopicNode from '@/models/management/TopicNode';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import TopicTree from '@/views/teacher/topics/TopicTree.vue';

const node = (id: number, name: string, parentId: number | null = null, sources = 0, questions = 0) =>
  new TopicNode({
    id,
    name,
    parentId,
    sequence: null,
    numberOfQuestions: questions,
    sources: Array.from({ length: sources }, (_, i) => ({ materialId: 'm1', chunkId: `m1:${id}${i}` })),
  } as TopicNode);

const tree = () => [
  node(1, 'Networks', null, 0),
  node(2, 'HTTP', 1, 2, 3),
  node(3, 'DNS', 1, 1),
  node(4, 'Databases', null, 0),
];

describe('TopicTree', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue(tree());
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.unstubAllGlobals();
    document.body.innerHTML = '';
  });

  const mountTree = async () => {
    const wrapper = mount(TopicTree, {
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };
  const vm = (wrapper: any) => wrapper.vm as any;

  test('shows the topics nested under their parents, with how many pieces each has and with its subtopics', async () => {
    const wrapper = await mountTree();

    const names = wrapper.findAll('[data-cy="TopicName"]').map((n) => n.text());
    expect(names).toEqual(['Networks', 'HTTP', 'DNS', 'Databases']);
    expect(wrapper.findAll('[data-cy="TopicPiecesChip"]').map((n) => n.text())).toEqual([
      '0 pieces, 3 with subtopics', '2 pieces', '1 piece', 'no pieces',
    ]);
    expect(wrapper.text()).toContain('3 question(s)');
  });

  test('says how to get started when the course has no topics', async () => {
    (RemoteServices.getTopicTree as any).mockResolvedValue([]);

    const wrapper = await mountTree();

    expect(wrapper.find('[data-cy="EmptyTree"]').exists()).toBe(true);
    expect(wrapper.find('[data-cy="TopicTree"]').exists()).toBe(false);
  });

  test('creates a topic at the top, or under another one', async () => {
    const create = vi.spyOn(RemoteServices, 'createTopic').mockResolvedValue(new Topic());
    const wrapper = await mountTree();

    vm(wrapper).askName('add', null);
    vm(wrapper).name = '  Security ';
    await vm(wrapper).saveName();
    vm(wrapper).askName('add', vm(wrapper).nodes[0]);
    vm(wrapper).name = 'TLS';
    await vm(wrapper).saveName();

    expect((create.mock.calls[0][0] as Topic).name).toBe('Security');
    expect((create.mock.calls[0][0] as Topic).parentId).toBeNull();
    expect((create.mock.calls[1][0] as Topic).parentId).toBe(1);
  });

  test('renames a topic', async () => {
    const update = vi.spyOn(RemoteServices, 'updateTopic').mockResolvedValue(new Topic());
    const wrapper = await mountTree();

    vm(wrapper).askName('rename', vm(wrapper).nodes[1]);
    expect(vm(wrapper).name).toBe('HTTP');
    vm(wrapper).name = 'Web';
    await vm(wrapper).saveName();

    expect((update.mock.calls[0][0] as Topic).id).toBe(2);
    expect((update.mock.calls[0][0] as Topic).name).toBe('Web');
  });

  test('a topic cannot be moved under itself or its subtopics', async () => {
    const wrapper = await mountTree();

    vm(wrapper).askParent(vm(wrapper).nodes[0]);

    expect(vm(wrapper).parentOptions.map((o: any) => o.value)).toEqual([null, 4]);
  });

  test('moves a topic under another one', async () => {
    const move = vi.spyOn(RemoteServices, 'moveTopic').mockResolvedValue(new TopicNode());
    const wrapper = await mountTree();

    vm(wrapper).askParent(vm(wrapper).nodes[2]);
    expect(vm(wrapper).newParentId).toBe(1);
    vm(wrapper).newParentId = 4;
    await vm(wrapper).saveParent();

    expect(move).toHaveBeenCalledWith(3, 4);
  });

  test('moves a topic to the top', async () => {
    const move = vi.spyOn(RemoteServices, 'moveTopic').mockResolvedValue(new TopicNode());
    const wrapper = await mountTree();

    vm(wrapper).askParent(vm(wrapper).nodes[2]);
    vm(wrapper).newParentId = null;
    await vm(wrapper).saveParent();

    expect(move).toHaveBeenCalledWith(3, null);
  });

  test('deletes a topic only after confirming, and warns that its subtopics move up', async () => {
    const del = vi.spyOn(RemoteServices, 'deleteTopic').mockResolvedValue(undefined as any);
    const confirm = vi.fn().mockReturnValue(false);
    vi.stubGlobal('confirm', confirm);
    const wrapper = await mountTree();

    await vm(wrapper).remove(vm(wrapper).nodes[0]);
    expect(del).not.toHaveBeenCalled();
    expect(confirm.mock.calls[0][0]).toContain('2 subtopic(s) move up');

    confirm.mockReturnValue(true);
    await vm(wrapper).remove(vm(wrapper).nodes[0]);
    expect((del.mock.calls[0][0] as Topic).id).toBe(1);
  });

  test('reads the tree again after a change', async () => {
    vi.spyOn(RemoteServices, 'moveTopic').mockResolvedValue(new TopicNode());
    const wrapper = await mountTree();
    const calls = (RemoteServices.getTopicTree as any).mock.calls.length;

    vm(wrapper).askParent(vm(wrapper).nodes[2]);
    await vm(wrapper).saveParent();

    expect((RemoteServices.getTopicTree as any).mock.calls.length).toBe(calls + 1);
  });

  test('a refused change is reported', async () => {
    vi.spyOn(RemoteServices, 'moveTopic').mockRejectedValue('A topic cannot be moved under itself');
    const wrapper = await mountTree();

    vm(wrapper).askParent(vm(wrapper).nodes[2]);
    await vm(wrapper).saveParent();

    expect(store.setError).toHaveBeenCalledWith('A topic cannot be moved under itself');
  });
});
