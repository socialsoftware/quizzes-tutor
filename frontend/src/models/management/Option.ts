export default class Option {
  id: number | null;
  content: string | null;
  correct: Boolean | null;

  constructor(jsonObj: any) {
    this.id = jsonObj.id;
    this.content = jsonObj.content;
    this.correct = jsonObj.correct;
  }
}
