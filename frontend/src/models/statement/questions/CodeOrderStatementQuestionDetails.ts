import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeOrderSlotStatementQuestionDetails from '@/models/statement/questions/CodeOrderSlotStatementQuestionDetails';

export default class CodeOrderStatementQuestionDetails extends StatementQuestionDetails {
  language: string = 'Java';
  orderSlots: CodeOrderSlotStatementQuestionDetails[] = [];

  constructor(jsonObj?: CodeOrderStatementQuestionDetails) {
    super(QuestionTypes.CodeOrder);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.orderSlots = jsonObj.orderSlots
        ? jsonObj.orderSlots.map(
            (spot: CodeOrderSlotStatementQuestionDetails) =>
              new CodeOrderSlotStatementQuestionDetails(spot)
          )
        : this.orderSlots;
    }
  }
}
