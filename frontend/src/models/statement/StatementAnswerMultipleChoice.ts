import StatementAnswer from './StatementAnswer';

export default class StatementAnswerMultipleChoice extends StatementAnswer {

  public optionId: number | null = null;

  constructor(jsonObj?: StatementAnswerMultipleChoice) {
    super(jsonObj)
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
    }
  }

  isAnswered(): boolean {
    return this.optionId !== null
  }
}
