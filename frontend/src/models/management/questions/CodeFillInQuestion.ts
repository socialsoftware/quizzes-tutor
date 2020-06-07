import Question from '../Question';

export default class CodeFillInQuestion extends Question {
  language: string = 'Java';
  code: string = '';
  codeFillInSpots: any[] = [];

  constructor(jsonObj?: CodeFillInQuestion) {
    super(jsonObj);
    if (jsonObj) {
      this.language = jsonObj.language || this.language;
      this.code = jsonObj.code || this.code;
      this.codeFillInSpots = jsonObj.codeFillInSpots || this.codeFillInSpots;
    }
  }
}
