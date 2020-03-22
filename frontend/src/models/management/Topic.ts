export default class Topic {
  id!: number;
  name!: string;
  numberOfQuestions!: number;

  constructor(jsonObj?: Topic) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
    }
  }
}
