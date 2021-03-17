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

  isCorrect(questionDetails: CodeFillInQuestionDetails): boolean {
    return (
      this.options.length === questionDetails.fillInSpots.length &&
      this.options.filter((op) => !op.correct).length == 0
    );
  }

  answerRepresentation(questionDetails: CodeFillInQuestionDetails): string {
    const answerRepr = [];
    for (const spot of questionDetails.fillInSpots) {
      let inserted = false;
      for (const option of this.options) {
        const optionInSpot = spot.options.filter((op) => op.id == option.id);
        if (optionInSpot.length > 0) {
          answerRepr.push((optionInSpot[0]?.sequence || 0) + 1);
          inserted = true;
          continue;
        }
      }
      if (!inserted) {
        answerRepr.push('-');
      }
    }
    return answerRepr.join(' | ');
  }
}
