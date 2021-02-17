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
    return (
      this.orderedSlots.length === questionDetails.codeOrderSlots.filter(os => os.order != null).length &&
      this.orderedSlots.filter(os => !os.correct).length == 0
    );
  }

  answerRepresentation(questionDetails: CodeOrderQuestionDetails): string {
    let counter = 1;
    let allIds = questionDetails.codeOrderSlots.map(x => x.id || 0)
    let options = Object.assign({}, ...allIds.sort((x,y) => x - y).map(x => ({[x]: ""+counter++})));
    return this.orderedSlots.map(x => options[x.slotId || 0] || "-").join(" | ");
  }
}
