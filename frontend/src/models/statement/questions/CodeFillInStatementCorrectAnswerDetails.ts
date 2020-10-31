import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeFillInSpotAnswerStatement from './CodeFillInSpotAnswerStatement';

// TODO: Missing correct data.
export default class CodeFillInStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctOptions!: CodeFillInSpotAnswerStatement[];

  constructor(jsonObj?: CodeFillInStatementCorrectAnswerDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      console.log('bla', jsonObj)
      this.correctOptions = jsonObj.correctOptions || [];
    }
  }
}
