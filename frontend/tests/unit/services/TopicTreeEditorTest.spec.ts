import { describe, expect, test } from 'vitest';
import TopicTreeNode from '@/models/management/TopicTreeNode';
import { TopicSource } from '@/models/management/TopicNode';
import {
  buildForest,
  linkToExisting,
  moveNode,
  nameProblems,
  removeNode,
  renameNode,
  subtreeKeys,
} from '@/services/TopicTreeEditor';

const source = (path: string) => new TopicSource({ materialId: 'm1', sectionPath: path });

const node = (key: string, parentKey: string | null = null, paths: string[] = [], existingTopicId: number | null = null) =>
  new TopicTreeNode({
    key,
    name: key,
    parentKey,
    existingTopicId,
    chunkCount: null,
    sources: paths.map(source),
  } as TopicTreeNode);

const keys = (nodes: TopicTreeNode[]) => nodes.map((n) => n.key);

describe('buildForest', () => {
  const forest = (nodes: TopicTreeNode[]) =>
    buildForest(nodes, (n) => n.key, (n) => n.parentKey, (n) => n.name);

  test('nests nodes under their parent and keeps the order among siblings', () => {
    const result = forest([node('a'), node('b', 'a'), node('c', 'a'), node('d')]);

    expect(result.map((item) => item.id)).toEqual(['a', 'd']);
    expect(result[0].children.map((item) => item.id)).toEqual(['b', 'c']);
  });

  test('a node whose parent is missing is shown at the top, not lost', () => {
    const result = forest([node('a'), node('orphan', 'gone')]);

    expect(result.map((item) => item.id)).toEqual(['a', 'orphan']);
  });

  test('a node that is its own parent does not disappear or loop', () => {
    expect(forest([node('a', 'a')]).map((item) => item.id)).toEqual(['a']);
  });

  test('builds deep trees', () => {
    const result = forest([node('a'), node('b', 'a'), node('c', 'b')]);

    expect(result[0].children[0].children[0].id).toBe('c');
  });

  test('works for numeric ids too', () => {
    const result = buildForest(
      [{ id: 1, parent: null }, { id: 2, parent: 1 }],
      (n) => n.id,
      (n) => n.parent,
      (n) => `Topic ${n.id}`
    );

    expect(result[0].children[0].title).toBe('Topic 2');
  });
});

describe('subtreeKeys', () => {
  test('is the node and everything below it', () => {
    const nodes = [node('a'), node('b', 'a'), node('c', 'b'), node('d')];

    expect([...subtreeKeys(nodes, 'a')].sort()).toEqual(['a', 'b', 'c']);
    expect([...subtreeKeys(nodes, 'd')]).toEqual(['d']);
  });
});

describe('renameNode', () => {
  test('renames one node and leaves the original list alone', () => {
    const nodes = [node('a'), node('b')];

    const result = renameNode(nodes, 'a', 'Renamed');

    expect(result.map((n) => n.name)).toEqual(['Renamed', 'b']);
    expect(nodes[0].name).toBe('a');
  });
});

describe('removeNode', () => {
  test('its subtopics move up to its parent', () => {
    const result = removeNode([node('a'), node('b', 'a'), node('c', 'b')], 'b');

    expect(keys(result)).toEqual(['a', 'c']);
    expect(result[1].parentKey).toBe('a');
  });

  test('its sections go to the topic above, so no part of the document is dropped', () => {
    const result = removeNode([node('a', null, ['A']), node('b', 'a', ['A > B'])], 'b');

    expect(result[0].sources.map((s) => s.sectionPath)).toEqual(['A', 'A > B']);
  });

  test('removing a top-level node promotes its subtopics to the top', () => {
    const result = removeNode([node('a'), node('b', 'a')], 'a');

    expect(result[0].parentKey).toBeNull();
  });

  test('an unknown key changes nothing', () => {
    expect(keys(removeNode([node('a')], 'nope'))).toEqual(['a']);
  });

  test('does not change the original list', () => {
    const nodes = [node('a'), node('b', 'a')];

    removeNode(nodes, 'a');

    expect(nodes[1].parentKey).toBe('a');
  });
});

describe('moveNode', () => {
  const nodes = [node('a'), node('b', 'a'), node('c', 'b'), node('d')];

  test('moves a node, with what is below it, under another one', () => {
    const result = moveNode(nodes, 'b', 'd')!;

    expect(result.find((n) => n.key === 'b')!.parentKey).toBe('d');
    expect(result.find((n) => n.key === 'c')!.parentKey).toBe('b');
  });

  test('moves a node to the top', () => {
    expect(moveNode(nodes, 'b', null)!.find((n) => n.key === 'b')!.parentKey).toBeNull();
  });

  test('refuses to move a node under itself or under its own subtopics', () => {
    expect(moveNode(nodes, 'a', 'a')).toBeNull();
    expect(moveNode(nodes, 'a', 'c')).toBeNull();
  });

  test('does not change the original list', () => {
    moveNode(nodes, 'b', 'd');

    expect(nodes[1].parentKey).toBe('a');
  });
});

describe('linkToExisting', () => {
  test('marks a node as an existing topic, or as a new one again', () => {
    const linked = linkToExisting([node('a')], 'a', 7);
    expect(linked[0].existingTopicId).toBe(7);

    expect(linkToExisting(linked, 'a', null)[0].existingTopicId).toBeNull();
  });
});

describe('nameProblems', () => {
  test('flags empty names, names repeated in the proposal and names the course already has', () => {
    const nodes = [node('a'), node('b'), node('c'), node('d')];
    const renamed = renameNode(renameNode(renameNode(nodes, 'a', '  '), 'b', 'Same'), 'c', 'Same');
    const final = renameNode(renamed, 'd', 'Taken');

    const problems = nameProblems(final, ['Taken']);

    expect(problems.get('a')).toBe('Needs a name');
    expect(problems.has('b')).toBe(false);
    expect(problems.get('c')).toBe('Another topic here has this name');
    expect(problems.get('d')).toBe('The course already has a topic with this name');
  });

  test('a node that joins an existing topic is not checked, its name is the topic', () => {
    const nodes = [node('a', null, [], 3)];

    expect(nameProblems(nodes, ['a']).size).toBe(0);
  });

  test('a clean proposal has no problems', () => {
    expect(nameProblems([node('a'), node('b', 'a')], ['Other']).size).toBe(0);
  });
});
