import { ISOtoString } from '@/services/ConvertDateService';

export default class Dashboard {
  id!: number;
  lastCheckFailedAnswers!: string;
  lastCheckWeeklyScores!: string;

  constructor(jsonObj?: Dashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.lastCheckFailedAnswers)
        this.lastCheckFailedAnswers = ISOtoString(
          jsonObj.lastCheckFailedAnswers
        );
      if (jsonObj.lastCheckWeeklyScores)
        this.lastCheckWeeklyScores = ISOtoString(jsonObj.lastCheckWeeklyScores);
    }
  }
}
