import Option from '@/models/management/Option';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/models/management/questions/Helpers';

export default class MultipleChoiceQuestionType extends QuestionDetails {
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
