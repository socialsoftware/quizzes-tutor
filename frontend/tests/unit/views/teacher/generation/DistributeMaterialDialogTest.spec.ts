import { flushPromises, mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import RemoteServices from '@/services/RemoteServices';
import { useStore } from '@/store';
import TopicNode from '@/models/management/TopicNode';
import TopicTreeNode from '@/models/management/TopicTreeNode';
import MaterialChunk from '@/models/management/generation/MaterialChunk';
import { created, existing, UNUSED } from '@/services/ChunkDistribution';

vi.mock('@/store', () => ({ useStore: vi.fn() }));

globalThis.visualViewport = { width: 1024, height: 768, addEventListener: vi.fn(), removeEventListener: vi.fn(), offsetLeft: 0, offsetTop: 0, pageLeft: 0, pageTop: 0, scale: 1 } as any;
globalThis.ResizeObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
globalThis.IntersectionObserver = class { observe() { } unobserve() { } disconnect() { } } as any;
import DistributeMaterialDialog from '@/views/teacher/generation/DistributeMaterialDialog.vue';

const chunk = (id: string, heading: string | null, topicId: number | null = null, text = `Text of ${id}`) =>
  new MaterialChunk({ id, position: Number(id.split(':')[1]), heading, text, topicId } as MaterialChunk);

const topic = (id: number, name: string, parentId: number | null = null) =>
  new TopicNode({ id, name, parentId, sequence: null, numberOfQuestions: 0, sources: [] } as TopicNode);

const BOOK = [
  chunk('b:0', null),
  chunk('b:1', 'Algebra'),
  chunk('b:2', 'Algebra > Numbers'),
  chunk('b:3', 'Algebra > Numbers > Rules'),
  chunk('b:4', 'Geometry'),
];

describe('DistributeMaterialDialog', () => {
  let store: any;

  beforeEach(() => {
    store = { setLoading: vi.fn(), clearLoading: vi.fn(), setError: vi.fn() };
    (useStore as any).mockReturnValue(store);
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue([topic(7, 'Algebra')]);
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  const mountDialog = async (chunks: MaterialChunk[]) => {
    vi.spyOn(RemoteServices, 'getMaterialChunks').mockResolvedValue(chunks);
    const wrapper = mount(DistributeMaterialDialog, {
      props: { dialog: true, materialId: 'b', materialName: 'book.pdf' },
      global: { plugins: [createVuetify({ components, directives })] },
      attachTo: document.body,
    });
    await flushPromises();
    return wrapper;
  };

  const vm = (wrapper: any) => wrapper.vm as any;
  const click = async (selector: string, index = 0) => {
    (document.body.querySelectorAll(selector)[index] as HTMLElement).click();
    await flushPromises();
  };

  test('a document no topic has yet starts from a suggestion made from its headings', async () => {
    const wrapper = await mountDialog(BOOK);

    expect(RemoteServices.getMaterialChunks).toHaveBeenCalledWith('b');
    expect(document.body.querySelector('[data-cy="SuggestedNotice"]')).not.toBeNull();
    // The course already has "Algebra"; its sections become new topics under it
    expect(vm(wrapper).destinations['b:1']).toBe(existing(7));
    expect(vm(wrapper).destinations['b:3']).toBe(created('Algebra > Numbers'));
    expect(vm(wrapper).destinations['b:0']).toBe(UNUSED);
    expect(document.body.querySelector('[data-cy="DistributionSummary"]')!.textContent).toContain('4 of 5 pieces');
  });

  test('a document already under topics shows where its pieces are, without suggesting', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', 7), chunk('b:1', 'Geometry', null)]);

    expect(document.body.querySelector('[data-cy="SuggestedNotice"]')).toBeNull();
    expect(vm(wrapper).destinations).toEqual({ 'b:0': existing(7), 'b:1': UNUSED });
    expect(Array.from(document.body.querySelectorAll('[data-cy="GroupDestination"]')).map((chip) => chip.textContent!.trim())).toEqual([
      'Algebra',
      'No topic',
    ]);
  });

  test('blocks follow the headings of the document, and a block shows its pieces when opened', async () => {
    await mountDialog(BOOK);

    expect(Array.from(document.body.querySelectorAll('[data-cy="GroupTitle"]')).map((title) => title.textContent)).toEqual([
      '(before the first heading)',
      'Algebra',
      'Numbers',
      'Rules',
      'Geometry',
    ]);
    expect(document.body.querySelectorAll('[data-cy="ChunkRow"]')).toHaveLength(0);

    await click('[data-cy="ToggleGroup"]', 1);

    expect(document.body.querySelector('[data-cy="ChunkRow"]')!.textContent).toContain('Text of b:1');
  });

  test('a topic chosen for a heading also goes to the headings below it', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', 7), chunk('b:1', 'Algebra > Numbers', 7), chunk('b:2', 'Geometry', 7)]);

    vm(wrapper).openPicker({ group: 0 });
    vm(wrapper).pickerValue = UNUSED;
    vm(wrapper).applyPicked();
    await flushPromises();

    expect(vm(wrapper).destinations).toEqual({ 'b:0': UNUSED, 'b:1': UNUSED, 'b:2': existing(7) });
  });

  test('a single piece can go to a new topic created on the spot, under the topic picked', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', 7), chunk('b:1', 'Algebra', 7)]);

    vm(wrapper).openPicker({ chunk: 'b:1' });
    vm(wrapper).pickerValue = existing(7);
    vm(wrapper).pickerNewName = 'Fractions';
    vm(wrapper).applyNewTopic();
    await flushPromises();

    expect(vm(wrapper).newTopics).toEqual([{ key: 'new-1', name: 'Fractions', parent: existing(7) }]);
    expect(vm(wrapper).destinations).toEqual({ 'b:0': existing(7), 'b:1': created('new-1') });
  });

  test('saving sends where every piece goes and tells the page', async () => {
    const save = vi.spyOn(RemoteServices, 'distributeMaterial').mockResolvedValue([]);
    const wrapper = await mountDialog(BOOK);

    await click('[data-cy="SaveDistribution"]');

    expect(save.mock.calls[0][0]).toBe('b');
    const nodes = save.mock.calls[0][1] as TopicTreeNode[];
    expect(nodes.map((node) => [node.key, node.name, node.parentKey, node.existingTopicId])).toEqual([
      ['t:7', 'Algebra', null, 7],
      ['n:Algebra > Numbers', 'Numbers', 't:7', null],
      ['n:Geometry', 'Geometry', null, null],
    ]);
    expect(nodes[1].sources.map((source) => source.chunkId)).toEqual(['b:2', 'b:3']);
    expect(wrapper.emitted('saved')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('a new topic named like one of the course blocks saving until it is renamed', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', 7)]);
    vm(wrapper).newTopics = [{ key: 'x', name: 'Algebra', parent: UNUSED }];
    vm(wrapper).destinations = { 'b:0': created('x') };
    await flushPromises();

    expect(document.body.querySelector('[data-cy="SaveDistribution"]')!.hasAttribute('disabled')).toBe(true);
    expect(document.body.querySelector('[data-cy="TopicProblem"]')!.textContent).toContain('already has a topic');
  });

  test('removing a new topic gives its pieces to the topic above', async () => {
    const wrapper = await mountDialog(BOOK);

    vm(wrapper).removeTopic({ value: created('Algebra > Numbers'), title: 'Numbers', depth: 1, isNew: true });
    await flushPromises();

    expect(vm(wrapper).destinations['b:2']).toBe(existing(7));
    expect(vm(wrapper).newTopics.some((t: any) => t.name === 'Numbers')).toBe(false);
  });

  test('closing with changes asks first', async () => {
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountDialog(BOOK);

    await click('[data-cy="CloseDistribution"]');

    expect(confirmation).toHaveBeenCalled();
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });

  test('a failed save is reported and the dialog stays open', async () => {
    vi.spyOn(RemoteServices, 'distributeMaterial').mockRejectedValue('Topic Algebra is duplicated');
    const wrapper = await mountDialog(BOOK);

    await click('[data-cy="SaveDistribution"]');

    expect(store.setError).toHaveBeenCalledWith('Topic Algebra is duplicated');
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });
});
