export default class Option {
  content: string | null;
  correct: Boolean | null;
  optionId: number | null;

  constructor(jsonObj: any) {
    this.content = jsonObj.content;
    this.correct = jsonObj.correct;
    this.optionId = jsonObj.optionId;
  }
}
