import StatementOption from '@/models/statement/StatementOption';
import { _ } from 'vue-underscore';

export default class StatementFillInSpot {
  sequence!: number;
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementFillInSpot) {
    if (jsonObj) {
      this.sequence = jsonObj.sequence || this.sequence;
      this.options = jsonObj.options
        ? _.shuffle(
            jsonObj.options.map(
              (option: StatementOption) => new StatementOption(option)
            )
          )
        : this.options;
    }
  }
}
