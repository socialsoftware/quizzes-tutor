import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeOrderStatementCorrectAnswerDetails from '@/models/statement/questions/CodeOrderStatementCorrectAnswerDetails';
import CodeOrderSlotStatementAnswerDetails from '@/models/statement/questions/CodeOrderSlotStatementAnswerDetails';

export default class CodeOrderStatementAnswerDetails extends StatementAnswerDetails {
  public orderedSlots!: CodeOrderSlotStatementAnswerDetails[];

  constructor(jsonObj?: CodeOrderStatementAnswerDetails) {
    super(QuestionTypes.CodeOrder);
    if (jsonObj) {
      this.orderedSlots = jsonObj.orderedSlots || [];
    }
  }

  isQuestionAnswered(): boolean {
    return this.orderedSlots != null && this.orderedSlots.length > 0;
  }

  isAnswerCorrect(
    correctAnswerDetails: CodeOrderStatementCorrectAnswerDetails
  ): boolean {
    for (const key in correctAnswerDetails.correctOrder) {
      const correct = correctAnswerDetails.correctOrder[key];
      if (!this.orderedSlots[key] && correct.order != null) {
        return false;
      } else if (
        correct.order != null &&
        correct.slotId != this.orderedSlots[key].slotId
      ) {
        return false;
      }
    }
    return (
      this.orderedSlots.length ===
      correctAnswerDetails.correctOrder.filter((os) => os.order != null).length
    );
  }
}
