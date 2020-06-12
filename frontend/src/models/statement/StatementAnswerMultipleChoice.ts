import StatementAnswer from './StatementAnswer';
import StatementCorrectAnswerCodeFillIn from './StatementCorrectAnswerCodeFillIn';
import StatementCorrectAnswerMultipleChoice from './StatementCorrectAnswerMultipleChoice';

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

  isCorrect(correctAnswer: StatementCorrectAnswerMultipleChoice): boolean {
    return this.optionId != null && this.optionId === correctAnswer.correctOptionId;
  }
}
