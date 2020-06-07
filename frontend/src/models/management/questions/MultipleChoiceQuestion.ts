import Option from '../Option';
import Question from '../Question';

export default class MultipleChoiceQuestion extends Question {
  options: Option[] = [new Option(), new Option(), new Option(), new Option()];

  constructor(jsonObj?: MultipleChoiceQuestion) {
    super(jsonObj);
    if (jsonObj) {
      this.options = jsonObj.options
        ? jsonObj.options.map((option: Option) => new Option(option))
        : this.options;
    }
  }
}
