import { ISOtoString } from '@/services/ConvertDateService';
import FailedAnswer from '@/models/dashboard/FailedAnswer';

export default class UpdatedFailedAnswers {
  lastCheckFailedAnswers!: string;
  failedAnswers!: FailedAnswer[];

  constructor(jsonObj?: UpdatedFailedAnswers) {
    if (jsonObj) {
      this.lastCheckFailedAnswers = ISOtoString(jsonObj.lastCheckFailedAnswers);
      this.failedAnswers = jsonObj.failedAnswers.map((failedAnswer: any) => {
        return new FailedAnswer(failedAnswer);
      });
    }
  }
}
