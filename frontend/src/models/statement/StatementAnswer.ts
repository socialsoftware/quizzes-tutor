export default class StatementAnswer {
  public quizQuestionId: number;
  public optionId: number | null = null;
  public timeTaken: number = 0;

  public constructor(questionId: number) {
    this.quizQuestionId = questionId;
  }
}
