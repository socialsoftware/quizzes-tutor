import StatementCorrectAnswer from './StatementCorrectAnswer';

export default class StatementCorrectAnswerMultipleChoice extends StatementCorrectAnswer {
  correctOptionId!: number;

  constructor(jsonObj?: StatementCorrectAnswerMultipleChoice) {
    super(jsonObj)
    if (jsonObj) {
      this.correctOptionId = jsonObj.correctOptionId;
    }
  }
}
