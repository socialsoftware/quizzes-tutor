import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';
import StatementQuestion from './StatementQuestion';

export default class StatementQuestionMultipleChoice extends StatementQuestion {
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestionMultipleChoice) {
    super(jsonObj)
    if (jsonObj) {
      if (jsonObj.options) {
        this.options = _.shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        );
      }
    }
  }
}