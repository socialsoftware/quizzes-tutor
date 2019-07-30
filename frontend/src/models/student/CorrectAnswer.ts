export interface CorrectAnswerDto {
  questionId: number;
  correctOptionId: number;
}

export default class CorrectAnswer {
  questionId!: number;
  correctOptionId!: number;

  constructor(answerJSON: CorrectAnswerDto) {
    this.questionId = answerJSON.questionId;
    this.correctOptionId = answerJSON.correctOptionId;
  }
}
