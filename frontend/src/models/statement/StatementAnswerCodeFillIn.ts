import StatementAnswer from './StatementAnswer';
import StatementAnswerCodeFillInOption from './StatementAnswerCodeFillInOption';

export default class StatementAnswerCodeFillIn extends StatementAnswer {
  public selectedOptions!: StatementAnswerCodeFillInOption[];

  constructor(jsonObj?: StatementAnswerCodeFillIn) {
    super(jsonObj);
    if (jsonObj) {
      this.selectedOptions = jsonObj.selectedOptions
        ? jsonObj.selectedOptions.map(
            (fill: StatementAnswerCodeFillInOption) =>
              new StatementAnswerCodeFillInOption(fill)
          )
        : this.selectedOptions;
    }
  }

  isAnswered(): boolean {
    return this.selectedOptions.length !== 0
  }
}
