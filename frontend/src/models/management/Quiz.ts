import Question from '@/models/management/Question';
import { ISOtoString } from '@/services/ConvertDateService';

export class Quiz {
  id!: number;
  number!: number;
  scramble!: boolean;
  qrCodeOnly!: boolean;
  timed!: boolean;
  oneWay!: boolean;
  title!: string;
  creationDate!: string;
  availableDate!: string;
  conclusionDate!: string;
  resultsDate!: string;
  timeToConclusion!: number;
  type!: string;
  series!: number;
  version!: string;
  numberOfQuestions!: number;
  numberOfAnswers!: number;

  questions: Question[] = [];

  constructor(jsonObj?: Quiz) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.scramble = jsonObj.scramble;
      this.qrCodeOnly = jsonObj.qrCodeOnly;
      this.timed = jsonObj.timed;
      this.oneWay = jsonObj.oneWay;
      this.title = jsonObj.title;
      this.type = jsonObj.type;
      this.series = jsonObj.series;
      this.version = jsonObj.version;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.timeToConclusion = jsonObj.timeToConclusion;

      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
      if (jsonObj.resultsDate)
        this.resultsDate = ISOtoString(jsonObj.resultsDate);

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
    }
  }
}
