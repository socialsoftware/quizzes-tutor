import QuestionDetails from '@/models/management/questions/QuestionDetails';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class CodeFillInQuestionDetails extends QuestionDetails {
  language: string = 'Java';
  code: string = '';
  fillInSpots: CodeFillInSpot[] = [];

  constructor(jsonObj?: CodeFillInQuestionDetails) {
    super(QuestionTypes.CodeFillIn);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.fillInSpots = jsonObj.fillInSpots
        ? jsonObj.fillInSpots.map(
            (option: CodeFillInSpot) => new CodeFillInSpot(option)
          )
        : this.fillInSpots;
    }
  }

  setAsNew(): void {
    this.fillInSpots.forEach((fill) => {
      fill.setAsNew();
    });
  }
}
