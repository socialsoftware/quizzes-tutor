import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeFillInStatementCorrectAnswerDetails from '@/models/statement/questions/CodeFillInStatementCorrectAnswerDetails';

//TODO: NEEDS TO BE FIXED
export default class CodeFillInStatementAnswerDetails extends StatementAnswerDetails {
  public optionId: number | null = null;

  constructor(jsonObj?: CodeFillInStatementAnswerDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
    }
  }

  isQuestionAnswered(): boolean {
    return this.optionId != null;
  }

  isAnswerCorrect(
    correctAnswerDetails: CodeFillInStatementCorrectAnswerDetails
  ): boolean {
    return (
      !!correctAnswerDetails &&
      this.optionId === correctAnswerDetails.correctOptionId
    );
  }
}
