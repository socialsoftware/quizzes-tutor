import TopicTreeNode from '@/models/management/TopicTreeNode';

// What a tree view needs: a title and the items below
export interface TreeItem {
  id: string | number;
  title: string;
  children: TreeItem[];
}

/**
 * Nests a flat list of nodes under their parents, keeping the order of the list among siblings.
 * A node whose parent is not in the list is shown at the top instead of being lost.
 */
export function buildForest<T>(
  nodes: T[],
  id: (node: T) => string | number,
  parent: (node: T) => string | number | null,
  title: (node: T) => string
): TreeItem[] {
  const items = new Map<string | number, TreeItem>();
  nodes.forEach((node) => items.set(id(node), { id: id(node), title: title(node), children: [] }));

  const roots: TreeItem[] = [];
  nodes.forEach((node) => {
    const item = items.get(id(node))!;
    const parentId = parent(node);
    const parentItem = parentId === null ? undefined : items.get(parentId);
    if (parentItem && parentItem !== item) parentItem.children.push(item);
    else roots.push(item);
  });
  return roots;
}

// The keys of a node and of everything below it
export function subtreeKeys(nodes: TopicTreeNode[], key: string): Set<string> {
  const keys = new Set<string>([key]);
  let grew = true;
  while (grew) {
    grew = false;
    for (const node of nodes) {
      if (node.parentKey !== null && keys.has(node.parentKey) && !keys.has(node.key)) {
        keys.add(node.key);
        grew = true;
      }
    }
  }
  return keys;
}

const copy = (node: TopicTreeNode): TopicTreeNode => new TopicTreeNode(node);

export function renameNode(nodes: TopicTreeNode[], key: string, name: string): TopicTreeNode[] {
  return nodes.map((node) => {
    const next = copy(node);
    if (node.key === key) next.name = name;
    return next;
  });
}

/**
 * Takes a node out of the proposal. Its subtopics move up to its parent, and so do its sections,
 * so no part of the document is dropped by accident (at the top there is no parent to receive them).
 */
export function removeNode(nodes: TopicTreeNode[], key: string): TopicTreeNode[] {
  const removed = nodes.find((node) => node.key === key);
  if (!removed) return nodes.map(copy);

  return nodes
    .filter((node) => node.key !== key)
    .map((node) => {
      const next = copy(node);
      if (node.parentKey === key) next.parentKey = removed.parentKey;
      if (node.key === removed.parentKey) next.sources = [...next.sources, ...removed.sources];
      return next;
    });
}

// Moves a node (with everything below it) under another one, or to the top. Null if that would make a loop.
export function moveNode(nodes: TopicTreeNode[], key: string, parentKey: string | null): TopicTreeNode[] | null {
  if (parentKey !== null && subtreeKeys(nodes, key).has(parentKey)) return null;
  return nodes.map((node) => {
    const next = copy(node);
    if (node.key === key) next.parentKey = parentKey;
    return next;
  });
}

// Marks a node as an existing topic of the course (or, with null, as a new one)
export function linkToExisting(nodes: TopicTreeNode[], key: string, topicId: number | null): TopicTreeNode[] {
  return nodes.map((node) => {
    const next = copy(node);
    if (node.key === key) next.existingTopicId = topicId;
    return next;
  });
}

/**
 * Names the teacher left empty or repeated: topic names are unique in a course, so saving would
 * fail on them. Nodes that join an existing topic keep that topic's name and are not checked.
 */
export function nameProblems(nodes: TopicTreeNode[], existingNames: string[]): Map<string, string> {
  const problems = new Map<string, string>();
  const taken = new Set(existingNames);
  const seen = new Set<string>();

  nodes.forEach((node) => {
    if (node.existingTopicId !== null) return;
    const name = node.name.trim();
    if (name === '') problems.set(node.key, 'Needs a name');
    else if (seen.has(name)) problems.set(node.key, 'Another topic here has this name');
    else if (taken.has(name)) problems.set(node.key, 'The course already has a topic with this name');
    seen.add(name);
  });
  return problems;
}
