import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';
import CodeOrderAnswerOrderedSlot from '@/models/management/questions/CodeOrderAnswerOrderedSlot';
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';

export default class CodeOrderAnswerDetails extends AnswerDetails {
  orderedSlots: CodeOrderAnswerOrderedSlot[] = [];

  constructor(jsonObj?: CodeOrderAnswerDetails) {
    super(QuestionTypes.CodeOrder);
    if (jsonObj) {
      this.orderedSlots = jsonObj.orderedSlots.map(
        (option: CodeOrderAnswerOrderedSlot) =>
          new CodeOrderAnswerOrderedSlot(option)
      );
    }
  }

  isCorrect(questionDetails: CodeOrderQuestionDetails): boolean {
    console.log(
      this.orderedSlots.length ===
        questionDetails.codeOrderSlots.filter((os) => os.order != null)
          .length && this.orderedSlots.filter((os) => !os.correct).length == 0
    );
    return (
      this.orderedSlots.length ===
        questionDetails.codeOrderSlots.filter((os) => os.order != null)
          .length && this.orderedSlots.filter((os) => !os.correct).length == 0
    );
  }

  answerRepresentation(questionDetails: CodeOrderQuestionDetails): string {
    return (
      this.orderedSlots.map((x) => '' + (x.sequence || 0)).join(' | ') || '-'
    );
  }
}
