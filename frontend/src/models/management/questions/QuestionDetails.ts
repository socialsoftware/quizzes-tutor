export default abstract class QuestionDetails {
  type!: string;

  constructor(type: string) {
    this.type = type;
  }

  abstract setAsNew(): void;
}
