export default class UnansweredDiscussionsInfo {
  quantity: number | null = null;

  constructor(jsonObj?: UnansweredDiscussionsInfo) {
    if (jsonObj) {
      this.quantity = jsonObj.quantity;
    }
  }
}
