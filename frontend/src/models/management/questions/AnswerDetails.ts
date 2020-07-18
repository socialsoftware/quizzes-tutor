export default abstract class AnswerDetails {
  type!: string;

  constructor(type: string) {
    this.type = type;
  }
}
