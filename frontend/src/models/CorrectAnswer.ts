interface ServerAnswer {
  questionId: number;
  correctOption: number;
}

export default class CorrectAnswer {
  questionId!: number;
  correctOption!: number;

  constructor(answerJSON: ServerAnswer) {
    this.questionId = answerJSON.questionId;
    this.correctOption = answerJSON.correctOption;
  }
}
