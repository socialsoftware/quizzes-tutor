import Question from '../Question';
import CodeFillInSpot from './CodeFillInSpot';

var DEFAULT_CONTENT: string = "Use the dropdown to complete the snippet of code below correctly.";

export default class CodeFillInQuestion extends Question {
  language: string = 'Java';
  code: string = '';
  fillInSpots: CodeFillInSpot[] = [];

  constructor(jsonObj?: CodeFillInQuestion) {
    super(jsonObj);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.fillInSpots =  jsonObj.fillInSpots
      ? jsonObj.fillInSpots.map((option: CodeFillInSpot) => new CodeFillInSpot(option))
      : this.fillInSpots;
    }
    if(!this.content){
      this.content = DEFAULT_CONTENT;
    }
  }
}
