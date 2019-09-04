export default class Option {
  id: number | null;
  number: number | null;
  content: string | null;
  correct: Boolean | null;

  constructor(jsonObj: any) {
    this.id = jsonObj.id;
    this.number = jsonObj.number;
    this.content = jsonObj.content;
    this.correct = jsonObj.correct;
  }
}
