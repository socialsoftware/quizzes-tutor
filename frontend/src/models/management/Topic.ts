export default class Topic {
  id!: number;
  name!: string;
  parentTopic: string | null = null;
  numberOfQuestions!: number;

  constructor(jsonObj?: Topic) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.parentTopic = jsonObj.parentTopic;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
    }
  }
}
