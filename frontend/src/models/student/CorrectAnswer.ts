export interface CorrectAnswerDto {
  questionId: number;
  correctOption: number;
}

export default class CorrectAnswer {
  questionId!: number;
  correctOption!: number;

  constructor(answerJSON: CorrectAnswerDto) {
    this.questionId = answerJSON.questionId;
    this.correctOption = answerJSON.correctOption;
  }
}
