export default class StatementAnswer {
  public optionId: number | null = null;
  public timeTaken: number = 0;
  public sequence!: number;

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
    }
  }
}
