export default class Answer {
  public quizQuestionId: number;
  public optionId: number | null = null;
  public timeTaken: Date | null = new Date();

  public constructor(questionId: number) {
    this.quizQuestionId = questionId;
  }
}
