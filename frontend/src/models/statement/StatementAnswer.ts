export default class StatementAnswer {
  public quizQuestionId: number;
  public optionId: number | null = null;
  public timeTaken: Date | null = new Date();

  public constructor(questionId: number) {
    this.quizQuestionId = questionId;
  }
}
