export default class StatementOption {
  optionId!: number;
  content!: string;

  constructor(jsonObj?: StatementOption) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.content = jsonObj.content;
    }
  }
}
