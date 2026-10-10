import { describe, expect, test } from 'vitest';
import MaterialChunk from '@/models/management/generation/MaterialChunk';
import TopicNode from '@/models/management/TopicNode';
import {
  created,
  destinationOptions,
  existing,
  groupAndBelow,
  groupChunks,
  nameProblems,
  removeNewTopic,
  suggest,
  toTree,
  UNUSED,
} from '@/services/ChunkDistribution';

const chunk = (id: string, heading: string | null) =>
  new MaterialChunk({ id, position: 0, heading, text: `text of ${id}`, topicId: null } as MaterialChunk);

const topic = (id: number, name: string, parentId: number | null = null) =>
  new TopicNode({ id, name, parentId, sequence: null, numberOfQuestions: 0, sources: [] } as TopicNode);

const BOOK = [
  chunk('b:0', null),
  chunk('b:1', 'Algebra'),
  chunk('b:2', 'Algebra > Numbers'),
  chunk('b:3', 'Algebra > Numbers'),
  chunk('b:4', 'Algebra > Numbers > Rules'),
  chunk('b:5', 'Algebra > Equations'),
  chunk('b:6', 'Geometry'),
];

describe('groupChunks', () => {
  test('pieces in a row under the same headings form one block', () => {
    const groups = groupChunks(BOOK);

    expect(groups.map((g) => g.title)).toEqual(['(before the first heading)', 'Algebra', 'Numbers', 'Rules', 'Equations', 'Geometry']);
    expect(groups[2].chunkIds).toEqual(['b:2', 'b:3']);
    expect(groups.map((g) => g.depth)).toEqual([0, 1, 2, 3, 2, 1]);
  });

  test('a block reaches the blocks of its subsections and stops at the next one of its level', () => {
    const groups = groupChunks(BOOK);

    expect(groupAndBelow(groups, 1).map((g) => g.title)).toEqual(['Algebra', 'Numbers', 'Rules', 'Equations']);
    expect(groupAndBelow(groups, 2).map((g) => g.title)).toEqual(['Numbers', 'Rules']);
    expect(groupAndBelow(groups, 0).map((g) => g.title)).toEqual(['(before the first heading)']);
  });
});

describe('suggest', () => {
  test('the first levels of headings become topics and each piece goes to its deepest one', () => {
    const { destinations, newTopics } = suggest(BOOK, [], 2);

    expect(newTopics.map((t) => [t.key, t.name, t.parent])).toEqual([
      ['Algebra', 'Algebra', UNUSED],
      ['Algebra > Numbers', 'Numbers', created('Algebra')],
      ['Algebra > Equations', 'Equations', created('Algebra')],
      ['Geometry', 'Geometry', UNUSED],
    ]);
    expect(destinations['b:0']).toBe(UNUSED);
    expect(destinations['b:1']).toBe(created('Algebra'));
    expect(destinations['b:4']).toBe(created('Algebra > Numbers'));
    expect(destinations['b:6']).toBe(created('Geometry'));
  });

  test('one level puts whole chapters under one topic each', () => {
    const { destinations, newTopics } = suggest(BOOK, [], 1);

    expect(newTopics.map((t) => t.name)).toEqual(['Algebra', 'Geometry']);
    expect(destinations['b:5']).toBe(created('Algebra'));
  });

  test('a topic the course already has is reused, whatever the case', () => {
    const { destinations, newTopics } = suggest(BOOK, [topic(7, 'algebra')], 2);

    expect(destinations['b:1']).toBe(existing(7));
    expect(newTopics.find((t) => t.name === 'Numbers')!.parent).toBe(existing(7));
    expect(newTopics.some((t) => t.name === 'Algebra')).toBe(false);
  });

  test('a heading repeated in two chapters gets two topics with different names', () => {
    const chunks = [chunk('c:0', 'One > Introduction'), chunk('c:1', 'Two > Introduction')];

    const { newTopics } = suggest(chunks, [], 2);

    expect(newTopics.map((t) => t.name)).toEqual(['One', 'Introduction', 'Two', 'Introduction (Two)']);
  });
});

describe('destinationOptions', () => {
  test('lists the course topics and the new ones under their parents, in tree order', () => {
    const options = destinationOptions([topic(1, 'Algebra'), topic(2, 'Geometry')], [
      { key: 'k', name: 'Numbers', parent: existing(1) },
    ]);

    expect(options.map((o) => [o.title, o.depth, o.isNew])).toEqual([
      ['Algebra', 0, false],
      ['Numbers', 1, true],
      ['Geometry', 0, false],
    ]);
  });
});

describe('removeNewTopic', () => {
  test('its pieces and subtopics go to its parent', () => {
    const result = removeNewTopic(
      { 'b:1': created('a'), 'b:2': created('b'), 'b:3': existing(5) },
      [
        { key: 'a', name: 'A', parent: existing(5) },
        { key: 'b', name: 'B', parent: created('a') },
      ],
      'a'
    );

    expect(result.destinations).toEqual({ 'b:1': existing(5), 'b:2': created('b'), 'b:3': existing(5) });
    expect(result.newTopics).toEqual([{ key: 'b', name: 'B', parent: existing(5) }]);
  });

  test('at the top its pieces are no longer used', () => {
    const result = removeNewTopic({ 'b:1': created('a') }, [{ key: 'a', name: 'A', parent: UNUSED }], 'a');

    expect(result.destinations['b:1']).toBe(UNUSED);
  });
});

describe('nameProblems', () => {
  test('flags empty names, repeated ones and names the course already has', () => {
    const problems = nameProblems(
      [
        { key: 'a', name: ' ', parent: UNUSED },
        { key: 'b', name: 'Numbers', parent: UNUSED },
        { key: 'c', name: 'Numbers', parent: UNUSED },
        { key: 'd', name: 'Algebra', parent: UNUSED },
      ],
      [topic(1, 'Algebra')]
    );

    expect([...problems.keys()]).toEqual(['a', 'c', 'd']);
  });
});

describe('toTree', () => {
  test('sends the topics that get pieces, the new ones above them, and leaves unused pieces out', () => {
    const topics = [topic(1, 'Algebra'), topic(2, 'Untouched')];
    const newTopics = [
      { key: 'n', name: 'Numbers ', parent: existing(1) },
      { key: 'r', name: 'Rules', parent: created('n') },
      { key: 'empty', name: 'Nothing here', parent: UNUSED },
    ];
    const destinations = { 'b:1': existing(1), 'b:2': UNUSED, 'b:4': created('r') };

    const nodes = toTree('b', BOOK, destinations, newTopics, topics);

    expect(nodes.map((n) => [n.key, n.name, n.parentKey, n.existingTopicId])).toEqual([
      ['t:1', 'Algebra', null, 1],
      ['n:n', 'Numbers', 't:1', null],
      ['n:r', 'Rules', 'n:n', null],
    ]);
    expect(nodes[0].sources.map((s) => s.chunkId)).toEqual(['b:1']);
    expect(nodes[1].sources).toEqual([]);
    expect(nodes[2].sources.map((s) => [s.materialId, s.chunkId])).toEqual([['b', 'b:4']]);
  });
});
