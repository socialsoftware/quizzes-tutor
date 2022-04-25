import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeFillInStatementCorrectAnswerDetails from '@/models/statement/questions/CodeFillInStatementCorrectAnswerDetails';
import CodeFillInSpotAnswerStatement from './CodeFillInSpotAnswerStatement';

export default class CodeFillInStatementAnswerDetails extends StatementAnswerDetails {
  public selectedOptions!: CodeFillInSpotAnswerStatement[];

  constructor(jsonObj?: CodeFillInStatementAnswerDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.selectedOptions = jsonObj.selectedOptions || [];
    }
  }

  isQuestionAnswered(): boolean {
    return this.selectedOptions != null && this.selectedOptions.length > 0;
  }

  isAnswerCorrect(
    correctAnswerDetails: CodeFillInStatementCorrectAnswerDetails
  ): boolean {
    const x =
      this.selectedOptions.length ===
        correctAnswerDetails.correctOptions.length &&
      !correctAnswerDetails.correctOptions.some(
        (op) =>
          !this.selectedOptions.some(
            (s) => s.optionId === op.optionId && s.sequence === op.sequence
          )
      );
    return x;
  }
}
