export default class StatementCorrectAnswer {
  quizQuestionId!: number;
  correctOptionId!: number;
  sequence!: number;

  constructor(jsonObj?: StatementCorrectAnswer) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.correctOptionId = jsonObj.correctOptionId;
      this.sequence = jsonObj.sequence;
    }
  }
}
