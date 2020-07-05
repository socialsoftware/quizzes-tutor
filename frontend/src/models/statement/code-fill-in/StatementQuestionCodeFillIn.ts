import StatementFillInSpot from '@/models/statement/StatementFillInSpot';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';
import StatementQuestion from '../StatementQuestion';

export default class StatementQuestionCodeFillIn extends StatementQuestion {
  language: string = 'Java';
  code: string = '';
  fillInSpots: StatementFillInSpot[] = [];

  constructor(jsonObj?: StatementQuestionCodeFillIn) {
    super(jsonObj)
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.fillInSpots = jsonObj.fillInSpots
        ? jsonObj.fillInSpots.map((fill: StatementFillInSpot) => new StatementFillInSpot(fill))
        : this.fillInSpots;
    }
  }
}