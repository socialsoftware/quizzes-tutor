import { ISOtoString } from '@/services/ConvertDateService';

export default class Dashboard {
  id!: number;
  lastCheckFailedAnswers!: string;
  lastCheckDifficultQuestions!: string;
  currentWeek!: string;

  constructor(jsonObj?: Dashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.lastCheckFailedAnswers)
        this.lastCheckFailedAnswers = ISOtoString(jsonObj.lastCheckFailedAnswers);
      if (jsonObj.lastCheckDifficultQuestions)
        this.lastCheckDifficultQuestions = ISOtoString(jsonObj.lastCheckDifficultQuestions);
      if (jsonObj.currentWeek)
        this.currentWeek = ISOtoString(jsonObj.currentWeek);
    }
  }
}
