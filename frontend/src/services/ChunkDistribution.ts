import MaterialChunk from '@/models/management/generation/MaterialChunk';
import TopicNode, { TopicSource } from '@/models/management/TopicNode';
import TopicTreeNode from '@/models/management/TopicTreeNode';
import { buildForest, TreeItem } from '@/services/TopicTreeEditor';

/**
 * Where a piece of a document goes: '' (no topic, so it is never used to generate questions),
 * `t:<id>` (a topic the course has) or `n:<key>` (a topic created when the teacher saves).
 */
export type Destination = string;
export const UNUSED: Destination = '';

export const existing = (topicId: number): Destination => `t:${topicId}`;
export const created = (key: string): Destination => `n:${key}`;

const HEADING_SEPARATOR = ' > ';

// A topic the teacher (or the suggestion) adds while distributing a document
export interface NewTopic {
  key: string;
  name: string;
  // UNUSED: at the top of the tree
  parent: Destination;
}

// Pieces in a row under the same headings: what the teacher sees and moves as one block
export interface ChunkGroup {
  index: number;
  heading: string | null;
  title: string;
  // How many headings are above the pieces (0: before the first heading)
  depth: number;
  chunkIds: string[];
}

export interface DestinationOption {
  value: Destination;
  title: string;
  depth: number;
  isNew: boolean;
}

const segmentsOf = (heading: string | null): string[] =>
  (heading ?? '')
    .split(HEADING_SEPARATOR)
    .map((segment) => segment.trim())
    .filter((segment) => segment !== '');

export function groupChunks(chunks: MaterialChunk[]): ChunkGroup[] {
  const groups: ChunkGroup[] = [];
  for (const chunk of chunks) {
    const last = groups[groups.length - 1];
    if (last && last.heading === chunk.heading) {
      last.chunkIds.push(chunk.id);
      continue;
    }
    const segments = segmentsOf(chunk.heading);
    groups.push({
      index: groups.length,
      heading: chunk.heading,
      title: segments.length ? segments[segments.length - 1] : '(before the first heading)',
      depth: segments.length,
      chunkIds: [chunk.id],
    });
  }
  return groups;
}

// The group and the ones right after it that are under its headings (its subsections)
export function groupAndBelow(groups: ChunkGroup[], index: number): ChunkGroup[] {
  const group = groups[index];
  const result = [group];
  if (!group.heading) return result;
  const prefix = group.heading + HEADING_SEPARATOR;
  for (let next = index + 1; next < groups.length; next++) {
    const heading = groups[next].heading;
    if (!heading || !heading.startsWith(prefix)) break;
    result.push(groups[next]);
  }
  return result;
}

const normalise = (name: string) => name.trim().toLowerCase();

/**
 * A first distribution from the headings of the document: the first `levels` levels of headings
 * become topics (a topic the course already has with that name is reused) and every piece goes
 * under the deepest of its headings that became one. Pieces before the first heading are left out.
 */
export function suggest(
  chunks: MaterialChunk[],
  topics: TopicNode[],
  levels: number
): { destinations: Record<string, Destination>; newTopics: NewTopic[] } {
  const existingByName = new Map<string, number>();
  topics.forEach((topic) => {
    if (!existingByName.has(normalise(topic.name))) existingByName.set(normalise(topic.name), topic.id);
  });
  const takenNames = new Set(topics.map((topic) => normalise(topic.name)));

  const byPath = new Map<string, Destination>();
  const newTopics: NewTopic[] = [];
  const nameOf = new Map<Destination, string>();
  topics.forEach((topic) => nameOf.set(existing(topic.id), topic.name));
  const destinations: Record<string, Destination> = {};

  for (const chunk of chunks) {
    const segments = segmentsOf(chunk.heading).slice(0, levels);
    let parent: Destination = UNUSED;
    segments.forEach((segment, depth) => {
      const path = segments.slice(0, depth + 1).join(HEADING_SEPARATOR);
      let destination = byPath.get(path);
      if (destination === undefined) {
        const found = existingByName.get(normalise(segment));
        if (found !== undefined) {
          destination = existing(found);
        } else {
          const name = uniqueName(segment, parent === UNUSED ? null : nameOf.get(parent) ?? null, takenNames);
          takenNames.add(normalise(name));
          destination = created(path);
          newTopics.push({ key: path, name, parent });
          nameOf.set(destination, name);
        }
        byPath.set(path, destination);
      }
      parent = destination;
    });
    destinations[chunk.id] = parent;
  }
  return { destinations, newTopics };
}

// Topic names are unique in a course: "Introduction" of a second chapter becomes "Introduction (Chapter 2)"
function uniqueName(name: string, parentName: string | null, taken: Set<string>): string {
  if (!taken.has(normalise(name))) return name;
  const withParent = parentName ? `${name} (${parentName})` : name;
  if (!taken.has(normalise(withParent))) return withParent;
  let counter = 2;
  while (taken.has(normalise(`${withParent} ${counter}`))) counter++;
  return `${withParent} ${counter}`;
}

/** The topics a piece can go to, in tree order: the course's topics with the new ones under their parents. */
export function destinationOptions(topics: TopicNode[], newTopics: NewTopic[]): DestinationOption[] {
  interface Entry {
    value: Destination;
    parent: Destination | null;
    name: string;
    isNew: boolean;
  }
  const entries: Entry[] = [
    ...topics.map((topic) => ({
      value: existing(topic.id),
      parent: topic.parentId === null ? null : existing(topic.parentId),
      name: topic.name,
      isNew: false,
    })),
    ...newTopics.map((topic) => ({
      value: created(topic.key),
      parent: topic.parent === UNUSED ? null : topic.parent,
      name: topic.name,
      isNew: true,
    })),
  ];
  const byValue = new Map(entries.map((entry) => [entry.value, entry]));
  const forest = buildForest(entries, (entry) => entry.value, (entry) => entry.parent, (entry) => entry.name);

  const options: DestinationOption[] = [];
  const walk = (items: TreeItem[], depth: number) =>
    items.forEach((item) => {
      const entry = byValue.get(item.id as string)!;
      options.push({ value: entry.value, title: entry.name, depth, isNew: entry.isNew });
      walk(item.children, depth + 1);
    });
  walk(forest, 0);
  return options;
}

/**
 * Takes a new topic out: its subtopics move up to its parent, and so do its pieces, so no part
 * of the document is dropped by accident (at the top there is no parent, so they go unused).
 */
export function removeNewTopic(
  destinations: Record<string, Destination>,
  newTopics: NewTopic[],
  key: string
): { destinations: Record<string, Destination>; newTopics: NewTopic[] } {
  const removed = newTopics.find((topic) => topic.key === key);
  if (!removed) return { destinations: { ...destinations }, newTopics: [...newTopics] };
  const value = created(key);

  const nextDestinations: Record<string, Destination> = {};
  Object.entries(destinations).forEach(([chunkId, destination]) => {
    nextDestinations[chunkId] = destination === value ? removed.parent : destination;
  });
  const nextTopics = newTopics
    .filter((topic) => topic.key !== key)
    .map((topic) => (topic.parent === value ? { ...topic, parent: removed.parent } : { ...topic }));
  return { destinations: nextDestinations, newTopics: nextTopics };
}

/**
 * Names that would make saving fail: empty, repeated among the new topics, or already a topic of
 * the course (names are unique in a course). Keyed by the new topic's key.
 */
export function nameProblems(newTopics: NewTopic[], topics: TopicNode[]): Map<string, string> {
  const problems = new Map<string, string>();
  const taken = new Set(topics.map((topic) => topic.name.trim()));
  const seen = new Set<string>();
  newTopics.forEach((topic) => {
    const name = topic.name.trim();
    if (name === '') problems.set(topic.key, 'Needs a name');
    else if (taken.has(name)) problems.set(topic.key, 'The course already has a topic with this name');
    else if (seen.has(name)) problems.set(topic.key, 'Another new topic has this name');
    seen.add(name);
  });
  return problems;
}

/**
 * What the Tutor saves: the topics that get pieces of the document (and the new ones above them),
 * each with its pieces. Pieces with no topic are in no node, so they end up under none.
 */
export function toTree(
  materialId: string,
  chunks: MaterialChunk[],
  destinations: Record<string, Destination>,
  newTopics: NewTopic[],
  topics: TopicNode[]
): TopicTreeNode[] {
  const sourcesOf = new Map<Destination, TopicSource[]>();
  chunks.forEach((chunk) => {
    const destination = destinations[chunk.id] ?? UNUSED;
    if (destination === UNUSED) return;
    if (!sourcesOf.has(destination)) sourcesOf.set(destination, []);
    sourcesOf.get(destination)!.push(new TopicSource({ materialId, chunkId: chunk.id }));
  });

  // A new topic is created when it gets pieces or holds a new topic that does
  const newByValue = new Map(newTopics.map((topic) => [created(topic.key), topic]));
  const needed = new Set<Destination>();
  const need = (destination: Destination) => {
    if (destination === UNUSED || needed.has(destination)) return;
    needed.add(destination);
    const topic = newByValue.get(destination);
    if (topic) need(topic.parent);
  };
  sourcesOf.forEach((_, destination) => need(destination));

  const nodes: TopicTreeNode[] = [];
  topics.forEach((topic) => {
    const value = existing(topic.id);
    if (!needed.has(value)) return;
    nodes.push(
      new TopicTreeNode({
        key: value,
        name: topic.name,
        parentKey: null,
        existingTopicId: topic.id,
        sources: sourcesOf.get(value) ?? [],
      })
    );
  });
  newTopics.forEach((topic) => {
    const value = created(topic.key);
    if (!needed.has(value)) return;
    nodes.push(
      new TopicTreeNode({
        key: value,
        name: topic.name.trim(),
        parentKey: topic.parent === UNUSED ? null : topic.parent,
        existingTopicId: null,
        sources: sourcesOf.get(value) ?? [],
      })
    );
  });
  return nodes;
}
