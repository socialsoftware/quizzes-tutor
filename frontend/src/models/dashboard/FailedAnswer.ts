import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { ISOtoString } from '@/services/ConvertDateService';

export default class FailedAnswer {
  id!: number;
  answered!: string;
  collected!: string;
  questionAnswerDto!: QuestionAnswer;

  constructor(jsonObj?: FailedAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.answered = jsonObj.answered ? 'Yes' : 'No';
      this.collected = ISOtoString(jsonObj.collected);
      this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);
    }
  }
}
