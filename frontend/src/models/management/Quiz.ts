import Question from '@/models/management/Question';
import { ISOtoString } from '@/services/ConvertDateService';

export class Quiz {
  id!: number;
  number!: number;
  scramble!: boolean;
  qrCodeOnly!: boolean;
  oneWay!: boolean;
  title!: string;
  creationDate!: string | undefined;
  availableDate!: string | undefined;
  conclusionDate!: string | undefined;
  secondsToConclusion!: number;
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
      this.oneWay = jsonObj.oneWay;
      this.title = jsonObj.title;
      this.type = jsonObj.type;
      this.series = jsonObj.series;
      this.version = jsonObj.version;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.availableDate = ISOtoString(jsonObj.availableDate);
      this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
      this.secondsToConclusion = jsonObj.secondsToConclusion;

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
    }
  }
}
