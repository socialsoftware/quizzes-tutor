import Option from '@/models/management/Option';
import AnswerType from '@/models/management/questions/AnswerType';
import { QuestionTypes } from '@/models/management/questions/Helpers';

export default class MultipleChoiceAnswerType extends AnswerType {
  option!: Option;

  constructor(jsonObj?: MultipleChoiceAnswerType) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.option = new Option(jsonObj.option);
    }
  }
}
