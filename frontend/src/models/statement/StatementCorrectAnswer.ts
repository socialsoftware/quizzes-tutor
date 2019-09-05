export default class StatementCorrectAnswer {
  quizQuestionId!: number;
  correctOptionId!: number;

  constructor(answer: StatementCorrectAnswer) {
    this.quizQuestionId = answer.quizQuestionId;
    this.correctOptionId = answer.correctOptionId;
  }
}
