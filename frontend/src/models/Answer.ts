interface ServerAnswer {
  id: number;
  correct_option: number;
}

export default class Answer {
  id!: number;
  correct_option!: number;

  constructor(answerJSON: ServerAnswer) {
    this.id = answerJSON.id;
    this.correct_option = answerJSON.correct_option;
  }
}
