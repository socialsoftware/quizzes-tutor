import StatementOption from '@/models/statement/StatementOption';
import { _ } from 'vue-underscore';

export default class CodeFillInSpotAnswerStatement {
  sequence!: number;
  optionId!: number | undefined;
  optionSequence!: number | undefined;

  constructor(jsonObj?: CodeFillInSpotAnswerStatement) {
    if (jsonObj) {
      this.sequence = jsonObj.sequence;
      this.optionId = jsonObj.optionId;
      this.optionSequence = jsonObj.optionSequence;
    }
  }
}
