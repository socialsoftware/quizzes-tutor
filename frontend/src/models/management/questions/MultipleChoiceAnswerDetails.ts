import Option from '@/models/management/Option';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class MultipleChoiceAnswerType extends AnswerDetails {
  option!: Option;

  constructor(jsonObj?: MultipleChoiceAnswerType) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.option = new Option(jsonObj.option);
    }
  }
}
