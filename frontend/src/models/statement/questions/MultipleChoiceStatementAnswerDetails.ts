import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';

export default class MultipleChoiceStatementAnswerDetails extends StatementAnswerDetails {
  public optionId: number | null = null;

  constructor(jsonObj?: MultipleChoiceStatementAnswerDetails) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
    }
  }

  isQuestionAnswered(): boolean {
    return this.optionId != null;
  }

  isAnswerCorrect(
    correctAnswerDetails: MultipleChoiceStatementCorrectAnswerDetails
  ): boolean {
    return (
      !!correctAnswerDetails &&
      this.optionId === correctAnswerDetails.correctOptionId
    );
  }
}
