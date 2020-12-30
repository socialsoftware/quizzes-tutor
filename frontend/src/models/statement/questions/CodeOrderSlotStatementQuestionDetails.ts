export default class CodeOrderSlotStatementQuestionDetails {
  id: number | null = null;
  content: string = '';

  constructor(jsonObj?: CodeOrderSlotStatementQuestionDetails) {
    if (jsonObj) {
      this.id = jsonObj.id || this.id;
      this.content = jsonObj.content || this.content;
    }
  }
}
