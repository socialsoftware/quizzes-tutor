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

  isCorrect(): boolean {
    return (
      this.orderedSlots.length > 0 &&
      this.orderedSlots.filter(os => !os.correct).length == 0
    );
  }

  answerRepresentation(questionDetails: CodeOrderQuestionDetails): string {
    console.log(questionDetails.codeOrderSlots, this.orderedSlots);
    let all = questionDetails.codeOrderSlots.length;
    let correctAnswer = questionDetails.codeOrderSlots.length;
    for (const key in questionDetails.codeOrderSlots) {
      let correct = questionDetails.codeOrderSlots[key];
      if (!this.orderedSlots[key] && correct.order != null){
        correctAnswer -= 1;
      }
      else if (correct.order != null && correct.id != this.orderedSlots[key].slotId) {
        correctAnswer -= 1;
      }
    }
    return `${correctAnswer}/${all}`;
  }
}
