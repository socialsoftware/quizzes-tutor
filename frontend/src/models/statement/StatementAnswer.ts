export default abstract class StatementAnswer {
  public timeTaken: number = 0;
  public sequence!: number;
  public questionAnswerId!: number;
  public type: string = "multiple_choice"

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.type = jsonObj.type;
    }
  }

  abstract isAnswered(): boolean;
}
