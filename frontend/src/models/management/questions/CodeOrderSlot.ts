export default class CodeOrderSlot {
  id: number | null = null;
  order: number | null = null;
  content: string = '';

  constructor(jsonObj?: CodeOrderSlot) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.order = jsonObj.order;
      this.content = jsonObj.content;
    }
  }

  setAsNew(): void {
    this.id = null;
  }
}
