import Question from '@/models/management/Question';
import Option from '@/models/management/Option';

export class QuestionAnswer {
  question!: Question;
  option!: Option;

  constructor(jsonObj?: QuestionAnswer) {
    if (jsonObj) {
      this.question = new Question(jsonObj.question);
      this.option = new Option(jsonObj.option);
    }
  }
}
