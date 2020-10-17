import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

// TODO: Missing correct data.
export default class CodeFillInStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOptionId: number | null = null;

  constructor(jsonObj?: CodeFillInStatementCorrectAnswerDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.correctOptionId = jsonObj.correctOptionId;
    }
  }
}
