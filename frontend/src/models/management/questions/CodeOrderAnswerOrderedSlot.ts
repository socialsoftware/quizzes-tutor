export default class CodeOrderAnswerOrderedSlot {
  slotId: number | null = null;
  order: number | null = null;
  correct: boolean = false;

  constructor(jsonObj?: CodeOrderAnswerOrderedSlot) {
    if (jsonObj) {
      this.slotId = jsonObj.slotId;
      this.order = jsonObj.order;
      this.correct = jsonObj.correct;
    }
  }
}
