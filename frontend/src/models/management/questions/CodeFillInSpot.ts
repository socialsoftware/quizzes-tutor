import Option from '../Option';

export default class CodeFillInSpot {
  id: number | null = null;
  sequence: number = 0;
  options: Option[] = [];

  constructor(jsonObj?: CodeFillInSpot) {
    if (jsonObj) {
      this.id = jsonObj.id || this.id;
      this.sequence = jsonObj.sequence || this.sequence;
      this.options = jsonObj.options
        ? jsonObj.options.map((option: Option) => new Option(option))
        : this.options;
    }
  }
}
