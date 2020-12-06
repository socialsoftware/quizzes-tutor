import Option from '@/models/management/Option';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';
import CodeFillInQuestionDetails from '@/models/management/questions/CodeFillInQuestionDetails';

export default class CodeFillInAnswerType extends AnswerDetails {
  options: Option[] = [];

  constructor(jsonObj?: CodeFillInAnswerType) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }

  isCorrect(): boolean {
    return this.options.length > 0 && this.options.filter(op => !op.correct).length == 0;
  }

  answerRepresentation(questionDetails: CodeFillInQuestionDetails): string {
    let correct = this.options.filter(op => op.correct).length;
    let all = questionDetails.fillInSpots.length;
    return `${correct}/${all}`;
  }
}
