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
    return this.orderedSlots != null;
  }

  // TODO: Fix this :D
  isAnswerCorrect(
    correctAnswerDetails: CodeOrderStatementCorrectAnswerDetails
  ): boolean {
    // var x =
    //   this.orderedSlots.length ===
    //     correctAnswerDetails.correctOptions.length &&
    //   !correctAnswerDetails.correctOptions.some(
    //     op =>
    //       !this.orderedSlots.some(
    //         s => s.optionId === op.optionId && s.sequence === op.sequence
    //       )
    //   );
    return false;
  }
}
