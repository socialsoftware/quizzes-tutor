import { ISOtoStringDayOnly } from '@/services/ConvertDateService';

export default class WeeklyScore {
  id!: number;
  quizzesAnswered!: number;
  questionsAnswered!: number;
  questionsUniquelyAnswered!: number;
  percentageCorrect!: number;
  improvedCorrectAnswers!: number;
  week!: string;

  constructor(jsonObj?: WeeklyScore) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.quizzesAnswered = jsonObj.quizzesAnswered;
      this.questionsAnswered = jsonObj.questionsAnswered;
      this.questionsUniquelyAnswered = jsonObj.questionsUniquelyAnswered;
      this.percentageCorrect = jsonObj.percentageCorrect;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.week = ISOtoStringDayOnly(jsonObj.week);
    }
  }
}
