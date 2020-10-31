import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeFillInStatementCorrectAnswerDetails from '@/models/statement/questions/CodeFillInStatementCorrectAnswerDetails';
import CodeFillInSpotAnswerStatement from './CodeFillInSpotAnswerStatement';

//TODO: NEEDS TO BE FIXED
export default class CodeFillInStatementAnswerDetails extends StatementAnswerDetails {
  public selectedOptions!: CodeFillInSpotAnswerStatement[];

  constructor(jsonObj?: CodeFillInStatementAnswerDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      console.log(jsonObj)
      this.selectedOptions = jsonObj.selectedOptions || [];
    }
  }

  isQuestionAnswered(): boolean {
    return this.selectedOptions != null;
  }

  isAnswerCorrect(
    correctAnswerDetails: CodeFillInStatementCorrectAnswerDetails
  ): boolean {
    return false;
  }
}
