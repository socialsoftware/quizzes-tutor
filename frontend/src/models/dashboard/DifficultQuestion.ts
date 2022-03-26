import { ISOtoString } from '@/services/ConvertDateService';
import Question from '@/models/management/Question';

export default class DifficultQuestion {
  id!: number;
  percentage!: number;
  removedDate!: string;
  removed!: boolean;
  questionDto!: Question;

  constructor(jsonObj?: DifficultQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.percentage = jsonObj.percentage;
      this.removedDate = ISOtoString(jsonObj.removedDate);
      this.removed = jsonObj.removed;
      this.questionDto = new Question(jsonObj.questionDto);
    }
  }
}
