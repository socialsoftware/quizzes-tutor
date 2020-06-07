import Question from '@/models/management/Question';
import Option from '@/models/management/Option';
import { QuestionFactory } from '@/services/QuestionFactory';

export class QuestionAnswer {
  question!: Question;
  option!: Option;

  constructor(jsonObj?: QuestionAnswer) {
    if (jsonObj) {
      this.question = QuestionFactory.createQuestion(jsonObj.question);
      this.option = new Option(jsonObj.option);
    }
  }
}
