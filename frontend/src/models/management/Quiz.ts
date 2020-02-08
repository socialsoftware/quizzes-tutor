import Question from '@/models/management/Question';

export class Quiz {
  id!: number;
  number!: number;
  scramble!: boolean;
  title!: string;
  date!: string | Date;
  creationDate!: string | undefined;
  availableDate!: string | undefined;
  conclusionDate!: string | undefined;
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
      this.title = jsonObj.title;
      this.type = jsonObj.type;
      this.series = jsonObj.series;
      this.version = jsonObj.version;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.date = jsonObj.date;
      this.creationDate = jsonObj.creationDate;
      this.availableDate = jsonObj.availableDate;
      this.conclusionDate = jsonObj.conclusionDate;

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
    }
  }

  get sortingDate(): number {
    if (this.date) {
      return new Date(this.date).getTime();
    }
    return 0;
  }

  get sortingCreationDate(): number {
    if (this.creationDate) {
      return new Date(this.creationDate).getTime();
    }
    return 0;
  }

  get sortingAvailableDate(): number {
    if (this.availableDate) {
      return new Date(this.availableDate).getTime();
    }
    return 0;
  }

  get sortingConclusionDate(): number {
    if (this.conclusionDate) {
      return new Date(this.conclusionDate).getTime();
    }
    return 0;
  }
}
