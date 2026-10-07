import { TopicSource } from '@/models/management/TopicNode';

// A topic of a proposed tree. Nothing here exists in the course yet: nodes point at their parent
// by `key`, and `existingTopicId` says the node is a topic the course already has
export default class TopicTreeNode {
  key!: string;
  name!: string;
  parentKey: string | null = null;
  existingTopicId: number | null = null;
  chunkCount: number | null = null;
  sources: TopicSource[] = [];

  constructor(jsonObj?: TopicTreeNode) {
    if (jsonObj) {
      this.key = jsonObj.key;
      this.name = jsonObj.name;
      this.parentKey = jsonObj.parentKey ?? null;
      this.existingTopicId = jsonObj.existingTopicId ?? null;
      this.chunkCount = jsonObj.chunkCount ?? null;
      this.sources = (jsonObj.sources ?? []).map((source) => new TopicSource(source));
    }
  }
}
