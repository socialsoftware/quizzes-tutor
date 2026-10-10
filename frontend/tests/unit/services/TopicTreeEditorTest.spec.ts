import { describe, expect, test } from 'vitest';
import { buildForest } from '@/services/TopicTreeEditor';

interface Node {
  key: string;
  parentKey: string | null;
  name: string;
}

const node = (key: string, parentKey: string | null = null): Node => ({ key, parentKey, name: key });

describe('buildForest', () => {
  const forest = (nodes: Node[]) =>
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
