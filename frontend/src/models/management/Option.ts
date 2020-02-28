export default class Option {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  correct: Boolean = false;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
    }
  }
}
