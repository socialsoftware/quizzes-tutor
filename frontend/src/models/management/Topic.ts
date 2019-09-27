export class Topic {
  name!: string;
  parentTopic: string | null = null;

  constructor(jsonObj?: Topic) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.parentTopic = jsonObj.parentTopic;
    }
  }
}
