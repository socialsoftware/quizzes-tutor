import { ISOtoString } from '@/services/ConvertDateService';

export default class WeeklyScore {
  id!: number;
  numberAnswered!: number;
  uniquelyAnswered!: number;
  percentageCorrect!: number;
  week!: string;

  constructor(jsonObj?: WeeklyScore) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numberAnswered = jsonObj.numberAnswered;
      this.uniquelyAnswered = jsonObj.uniquelyAnswered;
      this.percentageCorrect = jsonObj.percentageCorrect;
      this.week = ISOtoString(jsonObj.week);
    }
  }
}
