export class Topic {
  id!: number;
  name!: string;
  parentTopic: string | null = null;

  constructor(jsonObj?: Topic) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.parentTopic = jsonObj.parentTopic;
    }
  }
}
