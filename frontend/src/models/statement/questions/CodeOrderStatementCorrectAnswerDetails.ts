import CodeOrderAnswerOrderedSlot from '@/models/management/questions/CodeOrderAnswerOrderedSlot';
import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class CodeOrderStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOrder!: CodeOrderAnswerOrderedSlot[];

  constructor(jsonObj?: CodeOrderStatementCorrectAnswerDetails) {
    super(QuestionTypes.CodeOrder);
    if (jsonObj) {
      this.correctOrder = jsonObj.correctOrder || [];
    }
  }
}
