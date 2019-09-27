export default class StatementCorrectAnswer {
  quizQuestionId!: number;
  correctOptionId!: number;

  constructor(jsonObj?: StatementCorrectAnswer) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.correctOptionId = jsonObj.correctOptionId;
    }
  }
}
