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

const chunk = (id: string, heading: string | null, topicIds: number[] = [], text = `Text of ${id}`) =>
  new MaterialChunk({ id, position: Number(id.split(':')[1]), heading, text, topicIds } as MaterialChunk);

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
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue([topic(7, 'Algebra'), topic(8, 'Geometry')]);
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
  const chipsOf = (container: Element) => Array.from(container.querySelectorAll('[data-cy="DestinationChip"]'));
  const groupChips = (index: number) => chipsOf(document.body.querySelectorAll('[data-cy="GroupDestination"]')[index]);

  test('a new document starts with every piece under no topic, and nothing is suggested or created', async () => {
    const wrapper = await mountDialog(BOOK);

    expect(RemoteServices.getMaterialChunks).toHaveBeenCalledWith('b');
    expect(document.body.querySelector('[data-cy="SuggestedNotice"]')).toBeNull();
    expect(Object.values(vm(wrapper).destinations).every((list) => (list as string[]).length === 0)).toBe(true);
    expect(vm(wrapper).newTopics).toEqual([]);
    expect(document.body.querySelector('[data-cy="DistributionSummary"]')!.textContent).toContain('0 of 5 pieces');
    expect(Array.from(document.body.querySelectorAll('[data-cy="GroupDestination"]')).every((chip) => chip.textContent!.trim() === 'No topic')).toBe(true);
  });

  test('the suggestion made from the headings is only made when asked for', async () => {
    const wrapper = await mountDialog(BOOK);

    vm(wrapper).applySuggestion(2, false);
    await flushPromises();

    expect(document.body.querySelector('[data-cy="SuggestedNotice"]')).not.toBeNull();
    // The course already has "Algebra"; its sections become new topics under it
    expect(vm(wrapper).destinations['b:1']).toEqual([existing(7)]);
    expect(vm(wrapper).destinations['b:3']).toEqual([created('Algebra > Numbers')]);
    expect(vm(wrapper).destinations['b:0']).toEqual([]);
    expect(document.body.querySelector('[data-cy="DistributionSummary"]')!.textContent).toContain('4 of 5 pieces');
  });

  test('a topic the course has and a new one are shown in different colours, and the new one says so', async () => {
    const wrapper = await mountDialog(BOOK);

    vm(wrapper).applySuggestion(2, false);
    await flushPromises();

    const algebra = groupChips(1)[0];
    const numbers = groupChips(2)[0];
    expect(algebra.textContent!.trim()).toBe('Algebra');
    expect(numbers.textContent!.trim()).toBe('Numbers (new)');
    expect(algebra.className).toContain('primary');
    expect(numbers.className).toContain('warning');
    expect(numbers.className).not.toContain('primary');
    expect(document.body.querySelector('[data-cy="DistributionLegend"]')!.textContent).toContain('A new topic');
  });

  test('a document already under topics shows where its pieces are, without suggesting', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7]), chunk('b:1', 'Geometry', [])]);

    expect(document.body.querySelector('[data-cy="SuggestedNotice"]')).toBeNull();
    expect(vm(wrapper).destinations).toEqual({ 'b:0': [existing(7)], 'b:1': [] });
    expect(Array.from(document.body.querySelectorAll('[data-cy="GroupDestination"]')).map((chip) => chip.textContent!.trim())).toEqual([
      'Algebra',
      'No topic',
    ]);
  });

  test('a piece under several topics shows all of them', async () => {
    await mountDialog([chunk('b:0', 'Algebra', [7, 8])]);

    expect(groupChips(0).map((chip) => chip.textContent!.trim())).toEqual(['Algebra', 'Geometry']);
  });

  test('the document view shows every piece in full, with a bar for each topic it is under and faded when it is under none', async () => {
    await mountDialog([chunk('b:0', 'Algebra', [7, 8], 'First text'), chunk('b:1', 'Algebra', [7], 'Second text'), chunk('b:2', 'Geometry', [], 'Third text')]);

    await click('[data-cy="ViewReading"]');

    const pieces = Array.from(document.body.querySelectorAll('[data-cy="ReadingChunk"]'));
    expect(pieces.map((piece) => piece.querySelector('.chunk-text')!.textContent)).toEqual(['First text', 'Second text', 'Third text']);
    expect(pieces.map((piece) => piece.querySelectorAll('.stripe:not(.none)').length)).toEqual([2, 1, 0]);
    expect(pieces.map((piece) => piece.classList.contains('unused'))).toEqual([false, false, true]);
    // the heading is said once for the pieces under it
    expect(document.body.querySelectorAll('[data-cy="ReadingChunk"] .font-weight-medium')).toHaveLength(2);
  });

  test('a piece can be given its topics from the document view', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [], 'First text')]);
    await click('[data-cy="ViewReading"]');

    await click('[data-cy="ReadingChunk"]');

    expect(vm(wrapper).pickerOpen).toBe(true);
  });

  test('the view by topic shows how much text each topic is written from, subtopics included, and warns when it is too little', async () => {
    vi.spyOn(RemoteServices, 'getTopicTree').mockResolvedValue([
      new TopicNode({ id: 7, name: 'Algebra', parentId: null, sequence: null, numberOfQuestions: 0, sources: [] } as TopicNode),
      new TopicNode({ id: 9, name: 'Numbers', parentId: 7, sequence: null, numberOfQuestions: 0, sources: [] } as TopicNode),
      new TopicNode({ id: 8, name: 'Geometry', parentId: null, sequence: null, numberOfQuestions: 0, sources: [] } as TopicNode),
    ]);
    await mountDialog([
      chunk('b:0', 'Algebra', [7], 'a'.repeat(150)),
      chunk('b:1', 'Algebra > Numbers', [9], 'b'.repeat(120)),
      chunk('b:2', 'Geometry', [8], 'c'.repeat(50)),
      chunk('b:3', 'Appendix', [], 'd'.repeat(30)),
    ]);

    await click('[data-cy="ViewTopics"]');

    const summaries = Array.from(document.body.querySelectorAll('[data-cy="TopicSummary"]'));
    const sizes = summaries.map((summary) => summary.querySelector('[data-cy="TopicSize"]')!.textContent!.trim());
    expect(sizes).toEqual(['2 piece(s) · 270 characters', '1 piece(s) · 120 characters', '1 piece(s) · 50 characters']);
    expect(summaries.map((summary) => summary.querySelector('[data-cy="TopicTooLittle"]') !== null)).toEqual([false, true, true]);
    expect(document.body.querySelector('[data-cy="UnusedSummary"]')!.textContent).toContain('1 piece(s) · 30 characters');
  });

  test('a topic with nothing under it says so, and its text opens when asked', async () => {
    await mountDialog([chunk('b:0', 'Algebra', [7], 'x'.repeat(250))]);
    await click('[data-cy="ViewTopics"]');

    const summaries = Array.from(document.body.querySelectorAll('[data-cy="TopicSummary"]'));
    expect(summaries[1].querySelector('[data-cy="TopicEmpty"]')).not.toBeNull();
    expect(document.body.querySelectorAll('[data-cy="TopicPiece"]')).toHaveLength(0);

    await click('[data-cy="ToggleTopic"]');

    expect(document.body.querySelector('[data-cy="TopicPiece"] .chunk-text')!.textContent).toBe('x'.repeat(250));
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

  test('the topics chosen for a heading, one or several, also go to the headings below it', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra'), chunk('b:1', 'Algebra > Numbers'), chunk('b:2', 'Geometry')]);

    vm(wrapper).openPicker({ group: 0 });
    vm(wrapper).pickerValues = [existing(7), existing(8)];
    vm(wrapper).applyPicked();
    await flushPromises();

    expect(vm(wrapper).destinations).toEqual({
      'b:0': [existing(7), existing(8)],
      'b:1': [existing(7), existing(8)],
      'b:2': [],
    });
    expect(vm(wrapper).dirty).toBe(true);
  });

  test('leaving the topics empty takes the pieces out of every topic', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7, 8]), chunk('b:1', 'Geometry', [8])]);

    vm(wrapper).openPicker({ group: 0 });
    vm(wrapper).pickerValues = [];
    vm(wrapper).applyPicked();
    await flushPromises();

    expect(vm(wrapper).destinations).toEqual({ 'b:0': [], 'b:1': [existing(8)] });
  });

  test('a heading whose pieces are under different topics says so, and what is picked replaces them', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7]), chunk('b:1', 'Algebra', [8])]);

    expect(groupChips(0).map((chip) => chip.textContent!.trim())).toEqual(['Several topics']);

    vm(wrapper).openPicker({ group: 0 });
    await flushPromises();
    expect(vm(wrapper).pickerMixed).toBe(true);
    expect(vm(wrapper).pickerValues).toEqual([]);

    vm(wrapper).pickerValues = [existing(7)];
    vm(wrapper).applyPicked();
    await flushPromises();

    expect(vm(wrapper).destinations).toEqual({ 'b:0': [existing(7)], 'b:1': [existing(7)] });
  });

  test('a single piece has its own topics, apart from the heading', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7]), chunk('b:1', 'Algebra', [7])]);

    vm(wrapper).openPicker({ chunk: 'b:1' });
    expect(vm(wrapper).pickerValues).toEqual([existing(7)]);
    vm(wrapper).pickerValues = [existing(7), existing(8)];
    vm(wrapper).applyPicked();
    await flushPromises();

    expect(vm(wrapper).destinations).toEqual({ 'b:0': [existing(7)], 'b:1': [existing(7), existing(8)] });
    expect(groupChips(0).map((chip) => chip.textContent!.trim())).toEqual(['Several topics']);
  });

  test('a new topic created on the spot goes under the only topic picked, in its place', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7]), chunk('b:1', 'Algebra', [7])]);

    vm(wrapper).openPicker({ chunk: 'b:1' });
    vm(wrapper).pickerValues = [existing(7)];
    vm(wrapper).pickerNewName = 'Fractions';
    vm(wrapper).applyNewTopic();
    await flushPromises();

    expect(vm(wrapper).newTopics).toEqual([{ key: 'new-1', name: 'Fractions', parent: existing(7) }]);
    expect(vm(wrapper).destinations).toEqual({ 'b:0': [existing(7)], 'b:1': [created('new-1')] });
  });

  test('a new topic created while several are picked goes at the top and joins them', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7, 8])]);

    vm(wrapper).openPicker({ chunk: 'b:0' });
    vm(wrapper).pickerNewName = 'Revision';
    vm(wrapper).applyNewTopic();
    await flushPromises();

    expect(vm(wrapper).newTopics).toEqual([{ key: 'new-1', name: 'Revision', parent: UNUSED }]);
    expect(vm(wrapper).destinations['b:0']).toEqual([existing(7), existing(8), created('new-1')]);
  });

  test('saving sends where every piece goes and tells the page', async () => {
    const save = vi.spyOn(RemoteServices, 'distributeMaterial').mockResolvedValue([]);
    const wrapper = await mountDialog(BOOK);
    vm(wrapper).applySuggestion(2, false);
    await flushPromises();

    await click('[data-cy="SaveDistribution"]');

    expect(save.mock.calls[0][0]).toBe('b');
    const nodes = save.mock.calls[0][1] as TopicTreeNode[];
    expect(nodes.map((node) => [node.key, node.name, node.parentKey, node.existingTopicId])).toEqual([
      ['t:7', 'Algebra', null, 7],
      ['t:8', 'Geometry', null, 8],
      ['n:Algebra > Numbers', 'Numbers', 't:7', null],
    ]);
    expect(nodes[2].sources.map((source) => source.chunkId)).toEqual(['b:2', 'b:3']);
    expect(wrapper.emitted('saved')).toHaveLength(1);
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('a piece under two topics is sent in both', async () => {
    const save = vi.spyOn(RemoteServices, 'distributeMaterial').mockResolvedValue([]);
    await mountDialog([chunk('b:0', 'Algebra', [7, 8]), chunk('b:1', 'Geometry', [8])]);

    await click('[data-cy="SaveDistribution"]');

    const nodes = save.mock.calls[0][1] as TopicTreeNode[];
    expect(nodes.find((node) => node.key === 't:7')!.sources.map((s) => s.chunkId)).toEqual(['b:0']);
    expect(nodes.find((node) => node.key === 't:8')!.sources.map((s) => s.chunkId)).toEqual(['b:0', 'b:1']);
  });

  test('a new topic named like one of the course blocks saving until it is renamed', async () => {
    const wrapper = await mountDialog([chunk('b:0', 'Algebra', [7])]);
    vm(wrapper).newTopics = [{ key: 'x', name: 'Algebra', parent: UNUSED }];
    vm(wrapper).destinations = { 'b:0': [created('x')] };
    await flushPromises();

    expect(document.body.querySelector('[data-cy="SaveDistribution"]')!.hasAttribute('disabled')).toBe(true);
    expect(document.body.querySelector('[data-cy="TopicProblem"]')!.textContent).toContain('already has a topic');
  });

  test('removing a new topic gives its pieces to the topic above', async () => {
    const wrapper = await mountDialog(BOOK);
    vm(wrapper).applySuggestion(2, false);
    await flushPromises();

    vm(wrapper).removeTopic({ value: created('Algebra > Numbers'), title: 'Numbers', depth: 1, isNew: true });
    await flushPromises();

    expect(vm(wrapper).destinations['b:2']).toEqual([existing(7)]);
    expect(vm(wrapper).newTopics.some((t: any) => t.name === 'Numbers')).toBe(false);
  });

  test('closing with changes asks first', async () => {
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountDialog(BOOK);
    vm(wrapper).applySuggestion(2, false);
    await flushPromises();

    await click('[data-cy="CloseDistribution"]');

    expect(confirmation).toHaveBeenCalled();
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });

  test('closing without changes does not ask', async () => {
    const confirmation = vi.spyOn(window, 'confirm').mockReturnValue(false);
    const wrapper = await mountDialog(BOOK);

    await click('[data-cy="CloseDistribution"]');

    expect(confirmation).not.toHaveBeenCalled();
    expect(wrapper.emitted('update:dialog')![0]).toEqual([false]);
  });

  test('a failed save is reported and the dialog stays open', async () => {
    vi.spyOn(RemoteServices, 'distributeMaterial').mockRejectedValue('Topic Algebra is duplicated');
    const wrapper = await mountDialog(BOOK);
    vm(wrapper).applySuggestion(2, false);
    await flushPromises();

    await click('[data-cy="SaveDistribution"]');

    expect(store.setError).toHaveBeenCalledWith('Topic Algebra is duplicated');
    expect(wrapper.emitted('update:dialog')).toBeUndefined();
  });
});
