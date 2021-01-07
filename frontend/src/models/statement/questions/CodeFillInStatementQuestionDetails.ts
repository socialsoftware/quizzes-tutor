import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CodeFillInSpotStatement from '@/models/statement/questions/CodeFillInSpotStatement';
import { _ } from 'vue-underscore';

export default class CodeFillInStatementQuestionDetails extends StatementQuestionDetails {
  language: string = 'Java';
  code: string = '';
  fillInSpots: CodeFillInSpotStatement[] = [];

  constructor(jsonObj?: CodeFillInStatementQuestionDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.fillInSpots = jsonObj.fillInSpots
        ? jsonObj.fillInSpots.map(
            (fill: CodeFillInSpotStatement) => new CodeFillInSpotStatement(fill)
          )
        : this.fillInSpots;
    }
  }
}
