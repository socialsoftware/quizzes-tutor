export default class Answer {
  public questionId: number;
  public optionId: number | null = null;
  public timeTaken: Date | null = new Date();

  public constructor(questionId: number) {
    this.questionId = questionId;
  }
}
