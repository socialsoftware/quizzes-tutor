export default abstract class StatementCorrectAnswer {
  quizQuestionId!: number;
  sequence!: number;
  type: string = "multiple_choice";

  constructor(jsonObj?: StatementCorrectAnswer) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.sequence = jsonObj.sequence;
      this.type = jsonObj.type;
    }
  }
}
