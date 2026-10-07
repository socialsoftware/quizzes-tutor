import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import TopicNode, { TopicSource } from '@/models/management/TopicNode';
import TopicTreeNode from '@/models/management/TopicTreeNode';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import TopicSuggestionDialog from '@/views/teacher/generation/TopicSuggestionDialog.vue';

const proposalNode = (key: string, parentKey: string | null, paths: string[] = [], existingTopicId: number | null = null) =>
  new TopicTreeNode({
    key,
    name: key.split(' > ').pop(),
    parentKey,
    existingTopicId,
    chunkCount: null,
    sources: paths.map((path) => new TopicSource({ materialId: 'm1', sectionPath: path })),
  } as TopicTreeNode);

const proposal = () => [
  proposalNode('Networks', null, ['Networks']),
  proposalNode('Networks > HTTP', 'Networks', ['Networks > HTTP']),
  proposalNode('Networks > DNS', 'Networks', ['Networks > DNS']),
];

describe('TopicSuggestionDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getTopicSuggestion').mockResolvedValue(proposal());
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue([]);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async () => {
    const wrapper = mount(TopicSuggestionDialog, {
      props: { dialog: true, materialId: 'm1', materialName: 'book.pdf' },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  const vm = (wrapper: any) => wrapper.vm as any;
  const saveButton = () => document.body.querySelector('[data-cy="SaveTopics"]') as HTMLButtonElement;

  test('asks for the proposal of the document and shows its topics', async () => {
    await mountDialog();

    expect(RemoteServices.getTopicSuggestion).toHaveBeenCalledWith('m1');
    const text = document.body.textContent!;
    expect(text).toContain('Topics from "book.pdf"');
    ['Networks', 'HTTP', 'DNS'].forEach((name) => expect(text).toContain(name));
    expect(document.body.querySelector('[data-cy="ProposalSummary"]')!.textContent).toContain('3 new topic(s), 3 section(s) linked');
  });

  test('says so when the document has no sections to propose topics from', async () => {
    (RemoteServices.getTopicSuggestion as any).mockResolvedValue([]);

    await mountDialog();

    expect(document.body.querySelector('[data-cy="NoProposal"]')).not.toBeNull();
    expect(saveButton().disabled).toBe(true);
  });

  test('saves the proposal as it is, then tells the page and closes', async () => {
    const save = vi.spyOn(RemoteServices, 'saveTopicTree').mockResolvedValue([] as TopicNode[]);
    const wrapper = await mountDialog();

    saveButton().click();
    await flushPromises();

    expect(save).toHaveBeenCalledTimes(1);
    expect((save.mock.calls[0][0] as TopicTreeNode[]).map((n) => n.key)).toEqual([
      'Networks', 'Networks > HTTP', 'Networks > DNS',
    ]);
    expect(wrapper.emitted('saved')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('what the teacher edits is what is saved', async () => {
    const save = vi.spyOn(RemoteServices, 'saveTopicTree').mockResolvedValue([] as TopicNode[]);
    const wrapper = await mountDialog();

    // rename HTTP, move DNS to the top, remove nothing else
    vm(wrapper).askName(vm(wrapper).nodes[1]);
    vm(wrapper).name = 'Web protocol';
    vm(wrapper).saveName();
    vm(wrapper).askParent(vm(wrapper).nodes[2]);
    vm(wrapper).newParentKey = null;
    vm(wrapper).saveParent();
    await flushPromises();
    saveButton().click();
    await flushPromises();

    const sent = save.mock.calls[0][0] as TopicTreeNode[];
    expect(sent.find((n) => n.key === 'Networks > HTTP')!.name).toBe('Web protocol');
    expect(sent.find((n) => n.key === 'Networks > DNS')!.parentKey).toBeNull();
  });

  test('removing a topic hands its sections to the one above', async () => {
    const save = vi.spyOn(RemoteServices, 'saveTopicTree').mockResolvedValue([] as TopicNode[]);
    const wrapper = await mountDialog();

    vm(wrapper).remove(vm(wrapper).nodes[1]);
    await flushPromises();
    saveButton().click();
    await flushPromises();

    const sent = save.mock.calls[0][0] as TopicTreeNode[];
    expect(sent.map((n) => n.key)).toEqual(['Networks', 'Networks > DNS']);
    expect(sent[0].sources.map((s) => s.sectionPath)).toEqual(['Networks', 'Networks > HTTP']);
  });

  test('a topic cannot be moved under itself or under its own subtopics', async () => {
    const wrapper = await mountDialog();

    vm(wrapper).askParent(vm(wrapper).nodes[0]);

    const options = vm(wrapper).parentOptions.map((o: any) => o.value);
    expect(options).toEqual([null]);
  });

  test('a name the course already has blocks saving until it is changed', async () => {
    (RemoteServices.getTopicTree as any).mockResolvedValue([new TopicNode({ id: 9, name: 'HTTP' } as TopicNode)]);
    (RemoteServices.getTopicSuggestion as any).mockResolvedValue([
      proposalNode('Networks', null),
      proposalNode('Networks > HTTP', 'Networks'),
    ]);
    const wrapper = await mountDialog();

    expect(saveButton().disabled).toBe(true);
    expect(document.body.querySelector('[data-cy="NodeProblem"]')!.textContent).toContain('already has a topic');

    vm(wrapper).askName(vm(wrapper).nodes[1]);
    vm(wrapper).name = 'Web';
    vm(wrapper).saveName();
    await flushPromises();

    expect(saveButton().disabled).toBe(false);
  });

  test('a topic the course already has is joined, and can be created as a new one instead', async () => {
    (RemoteServices.getTopicSuggestion as any).mockResolvedValue([
      proposalNode('Networks', null, [], 4),
    ]);
    const wrapper = await mountDialog();

    expect(document.body.querySelector('[data-cy="JoinsExisting"]')).not.toBeNull();
    expect(saveButton().disabled).toBe(false);

    vm(wrapper).unlink(vm(wrapper).nodes[0]);
    await flushPromises();

    expect(vm(wrapper).nodes[0].existingTopicId).toBeNull();
    expect(document.body.querySelector('[data-cy="JoinsExisting"]')).toBeNull();
  });

  test('a failed save is reported and the dialog stays open', async () => {
    vi.spyOn(RemoteServices, 'saveTopicTree').mockRejectedValue('Duplicate topic: HTTP');
    const wrapper = await mountDialog();

    saveButton().click();
    await flushPromises();

    expect(store.setError).toHaveBeenCalledWith('Duplicate topic: HTTP');
    expect(wrapper.emitted('saved')).toBeUndefined();
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });

  test('a proposal that cannot be loaded is reported', async () => {
    (RemoteServices.getTopicSuggestion as any).mockRejectedValue('The question generation service is not available');

    await mountDialog();

    expect(store.setError).toHaveBeenCalledWith('The question generation service is not available');
  });
});
