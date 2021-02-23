export default class CodeOrderAnswerOrderedSlot {
  slotId: number | null = null;
  order: number | null = null;
  correct: boolean = false;
  sequence: number | null = null;

  constructor(jsonObj?: CodeOrderAnswerOrderedSlot) {
    if (jsonObj) {
      this.slotId = jsonObj.slotId;
      this.order = jsonObj.order;
      this.correct = jsonObj.correct;
      this.sequence = jsonObj.sequence;
    }
  }
}
