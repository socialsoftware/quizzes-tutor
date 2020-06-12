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
  duplicate(): MultipleChoiceQuestion {
    const dup = new MultipleChoiceQuestion(this);
    dup.id = null;
    dup.image = null;
    dup.options.forEach(spot => {
      spot.id = null;
    });
    return dup;
  }

}
