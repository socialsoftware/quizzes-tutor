import Question from '@/models/management/Question';
import Option from '@/models/management/Option';
import AnswerType from '@/models/management/questions/AnswerType';
import MultipleChoiceAnswerType from '@/models/management/questions/MultipleChoiceAnswerType';
import { createAnswerType } from '@/models/management/questions/Helpers';

export class QuestionAnswer {
  question!: Question;
  answer: AnswerType = new MultipleChoiceAnswerType();

  constructor(jsonObj?: QuestionAnswer) {
    if (jsonObj) {
      this.question = new Question(jsonObj.question);
      this.answer = createAnswerType(jsonObj);
    }
  }
}
