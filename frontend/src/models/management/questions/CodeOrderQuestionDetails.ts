import QuestionDetails from '@/models/management/questions/QuestionDetails';
import CodeOrderSlot from '@/models/management/questions/CodeOrderSlot';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class CodeOrderQuestionDetails extends QuestionDetails {
  language: string = 'Java';
  codeOrderSlots: CodeOrderSlot[] = [];

  constructor(jsonObj?: CodeOrderQuestionDetails) {
    super(QuestionTypes.CodeOrder);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.codeOrderSlots = jsonObj.codeOrderSlots
        ? jsonObj.codeOrderSlots.map(
            (slot: CodeOrderSlot) => new CodeOrderSlot(slot)
          )
        : this.codeOrderSlots;
    }
  }

  setAsNew(): void {
    this.codeOrderSlots.forEach((slot) => {
      slot.setAsNew();
    });
  }
}
