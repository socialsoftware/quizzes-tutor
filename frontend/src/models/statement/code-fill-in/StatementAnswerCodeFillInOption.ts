
export default class StatementAnswerCodeFillInOption {
  public optionId: number | null = null;
  public sequence: number | null = null;

  constructor(jsonObj?: StatementAnswerCodeFillInOption) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.sequence = jsonObj.sequence;
    }
  }

  isCorrect(correctAnswerSlot: any): any {
    return this.optionId != null && this.optionId === correctAnswerSlot.optionId;
  }
}
