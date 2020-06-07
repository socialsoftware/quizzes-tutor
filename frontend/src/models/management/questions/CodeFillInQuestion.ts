import Question from '../Question';

var DEFAULT_CONTENT: string = "Use the dropdown to complete the snippet of code below correctly.";

export default class CodeFillInQuestion extends Question {
  language: string = 'Java';
  code: string = '';
  fillInSpots: any[] = [];

  constructor(jsonObj?: CodeFillInQuestion) {
    super(jsonObj);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.fillInSpots = jsonObj.fillInSpots || this.fillInSpots;
    }
    if(!this.content){
      this.content = DEFAULT_CONTENT;
    }
  }
}
