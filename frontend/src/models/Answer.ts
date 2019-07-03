export default class Answer {
  public questionId: number;
  public option: number | null;
  public timeTaken: Date | null;

  public constructor(
    questionId: number,
    option: number | null,
    time_taken: Date | null
  ) {
    this.questionId = questionId;
    this.option = option;
    this.timeTaken = time_taken;
  }
}
