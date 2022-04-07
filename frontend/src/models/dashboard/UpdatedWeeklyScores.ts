import { ISOtoString } from '@/services/ConvertDateService';
import WeeklyScore from '@/models/dashboard/WeeklyScore';

export default class UpdatedWeeklyScores {
  lastCheckWeeklyScores!: string;
  weeklyScores!: WeeklyScore[];

  constructor(jsonObj?: UpdatedWeeklyScores) {
    if (jsonObj) {
      this.lastCheckWeeklyScores = ISOtoString(jsonObj.lastCheckWeeklyScores);
      this.weeklyScores = jsonObj.weeklyScores.map((weeklyScore: any) => {
        return new WeeklyScore(weeklyScore);
      });
    }
  }
}
