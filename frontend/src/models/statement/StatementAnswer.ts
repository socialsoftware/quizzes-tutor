export default class StatementAnswer {
  public quizQuestionId!: number;
  public optionId: number | null = null;
  public timeTaken: number = 0;

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.optionId = jsonObj.optionId;
      this.timeTaken = jsonObj.timeTaken;
    }
  }
}
