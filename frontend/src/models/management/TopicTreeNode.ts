import { TopicSource } from '@/models/management/TopicNode';

// A topic to save with the pieces of documents under it. Nodes point at their parent by `key`,
// because some do not exist yet; `existingTopicId` says the node is a topic the course already has
export default class TopicTreeNode {
  key!: string;
  name!: string;
  parentKey: string | null = null;
  existingTopicId: number | null = null;
  sources: TopicSource[] = [];

  constructor(jsonObj?: TopicTreeNode) {
    if (jsonObj) {
      this.key = jsonObj.key;
      this.name = jsonObj.name;
      this.parentKey = jsonObj.parentKey ?? null;
      this.existingTopicId = jsonObj.existingTopicId ?? null;
      this.sources = (jsonObj.sources ?? []).map((source) => new TopicSource(source));
    }
  }
}
