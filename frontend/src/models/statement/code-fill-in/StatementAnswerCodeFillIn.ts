import StatementAnswer from '../StatementAnswer';
import StatementAnswerCodeFillInOption from './StatementAnswerCodeFillInOption';
import StatementCorrectAnswerCodeFillIn from './StatementCorrectAnswerCodeFillIn';

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

  isCorrect(correctAnswer: StatementCorrectAnswerCodeFillIn): boolean {
    return this.selectedOptions
      .map(opt => opt.isCorrect(correctAnswer.correctOptions.find(ca => ca.sequence === opt.sequence)))
      .filter(x => !x)
      .length === 0
  }
}
