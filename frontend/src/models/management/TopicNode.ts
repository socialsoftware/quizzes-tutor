// A piece (chunk) of a course material a topic is taught from
export class TopicSource {
  materialId!: string;
  chunkId!: string;

  constructor(jsonObj?: { materialId: string; chunkId: string }) {
    if (jsonObj) {
      this.materialId = jsonObj.materialId;
      this.chunkId = jsonObj.chunkId;
    }
  }
}

// A topic as a node of the course's topic tree. Teachers only: it carries the pieces of documents
export default class TopicNode {
  id!: number;
  name!: string;
  parentId: number | null = null;
  sequence: number | null = null;
  numberOfQuestions: number = 0;
  sources: TopicSource[] = [];

  constructor(jsonObj?: TopicNode) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.parentId = jsonObj.parentId ?? null;
      this.sequence = jsonObj.sequence ?? null;
      this.numberOfQuestions = jsonObj.numberOfQuestions ?? 0;
      this.sources = (jsonObj.sources ?? []).map((source) => new TopicSource(source));
    }
  }

  // The id of this topic and of every topic below it in `all`
  static subtreeIds(all: TopicNode[], rootId: number): Set<number> {
    const ids = new Set<number>([rootId]);
    let grew = true;
    while (grew) {
      grew = false;
      for (const node of all) {
        if (node.parentId !== null && ids.has(node.parentId) && !ids.has(node.id)) {
          ids.add(node.id);
          grew = true;
        }
      }
    }
    return ids;
  }

  // The pieces of a topic and of its subtopics, each piece once
  static subtreeSources(all: TopicNode[], rootId: number): TopicSource[] {
    const ids = TopicNode.subtreeIds(all, rootId);
    const seen = new Set<string>();
    const sources: TopicSource[] = [];
    for (const node of all) {
      if (!ids.has(node.id)) continue;
      for (const source of node.sources) {
        if (!seen.has(source.chunkId)) {
          seen.add(source.chunkId);
          sources.push(source);
        }
      }
    }
    return sources;
  }
}
