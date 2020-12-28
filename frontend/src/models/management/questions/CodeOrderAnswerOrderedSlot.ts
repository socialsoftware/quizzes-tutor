export default class CodeOrderAnswerOrderedSlot {
  spotId: number | null = null;
  order: number | null = null;
  correct: boolean = false;

  constructor(jsonObj?: CodeOrderAnswerOrderedSlot) {
    if (jsonObj) {
      this.spotId = jsonObj.spotId;
      this.order = jsonObj.order;
      this.correct = jsonObj.correct;
    }
  }
}
