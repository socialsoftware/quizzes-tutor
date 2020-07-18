export default abstract class StatementAnswerDetails {
  type!: string;

  constructor(type: string) {
    this.type = type;
  }

  abstract isQuestionAnswered(): boolean;
}
