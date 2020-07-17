import Option from '@/models/management/Option';
import QuestionType from '@/models/management/questions/QuestionType';
import { QuestionTypes } from '@/models/management/questions/Helpers';

export default class MultipleChoiceQuestionType extends QuestionType {
  options: Option[] = [new Option(), new Option(), new Option(), new Option()];

  constructor(jsonObj?: MultipleChoiceQuestionType) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }

  duplicate(): void {
    this.options.forEach(option => {
      option.id = null;
    });
  }
}
