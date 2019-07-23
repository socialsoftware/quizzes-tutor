export default class Answer {
  public questionId: number;
  public option: number | null = null;
  public timeTaken: Date | null = new Date();

  public constructor(questionId: number) {
    this.questionId = questionId;
  }
}
