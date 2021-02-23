export default class CodeOrderSlotStatementAnswerDetails {
  slotId: number | null = null;
  order: number | null = null;

  constructor(jsonObj?: CodeOrderSlotStatementAnswerDetails) {
    if (jsonObj) {
      this.slotId = jsonObj.slotId || this.slotId;
      this.order = jsonObj.order || this.order;
    }
  }
}
