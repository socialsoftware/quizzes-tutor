import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class MultipleChoiceStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOptionId: number | null = null;

  constructor(jsonObj?: MultipleChoiceStatementCorrectAnswerDetails) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.correctOptionId = jsonObj.correctOptionId;
    }
  }
}
