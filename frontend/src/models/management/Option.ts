export default class Option {
  id: number | null = null;
  number!: number | null;
  content: string = '';
  correct: Boolean = false;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
    }
  }
}
