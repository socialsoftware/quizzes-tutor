import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import StatementOption from '@/models/statement/StatementOption';

function shuffle(array: any[]) {
  let currentIndex = array.length, randomIndex;
  while (currentIndex != 0) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex--;
    [array[currentIndex], array[randomIndex]] = [array[randomIndex], array[currentIndex]];
  }
  return array;
}

export default class MultipleChoiceStatementQuestionDetails extends StatementQuestionDetails {
  options: StatementOption[] = [];

  constructor(jsonObj?: MultipleChoiceStatementQuestionDetails) {
    super(QuestionTypes.MultipleChoice);
    if (jsonObj) {
      if (jsonObj.options) {
        this.options = shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        );
      }
    }
  }
}
