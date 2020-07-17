export default abstract class QuestionType {
  type!: string;

  constructor(type: string) {
    this.type = type;
  }

  abstract duplicate(): void;
}
