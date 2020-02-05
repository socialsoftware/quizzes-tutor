export default class StatementAnswer {
  public id!: number;
  public quizAnswerId!: number;
  public quizQuestionId!: number;
  public answerDate!: string | Date;
  public optionId: number | null = null;
  public timeTaken: number = 0;
  public sequence!: number;

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.optionId = jsonObj.optionId;
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;

      if (jsonObj.answerDate) this.answerDate = new Date(jsonObj.answerDate);
    }
  }
}
