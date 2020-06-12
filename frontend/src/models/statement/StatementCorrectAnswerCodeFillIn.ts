import StatementCorrectAnswer from './StatementCorrectAnswer';

export default class StatementCorrectAnswerCodeFillIn extends StatementCorrectAnswer {
  correctOptions!: any[];


  constructor(jsonObj?: StatementCorrectAnswerCodeFillIn) {
    super(jsonObj)
    if (jsonObj) {
      this.correctOptions = jsonObj.correctOptions;
    }
  }
}
