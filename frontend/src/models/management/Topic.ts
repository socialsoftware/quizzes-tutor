export default class Topic {
  id!: number;
  name!: string;
  numberOfQuestions!: number;
  // The topic above this one in the course's tree; null at the top
  parentId?: number | null = null;
  sequence?: number | null = null;

  constructor(jsonObj?: Topic) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.parentId = jsonObj.parentId ?? null;
      this.sequence = jsonObj.sequence ?? null;
    }
  }
}
