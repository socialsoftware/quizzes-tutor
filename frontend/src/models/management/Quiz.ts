import Question from "@/models/management/Question";

export class Quiz {
  id!: number;
  number!: number;
  scramble!: boolean;
  title!: string;
  date!: string;
  creationDate!: string;
  availableDate!: string;
  conclusionDate!: string;
  type!: string;
  year!: number;
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
      this.year = jsonObj.year;
      this.series = jsonObj.series;
      this.version = jsonObj.version;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAnswers = jsonObj.numberOfAnswers;

      if (jsonObj.date) this.date = new Date(jsonObj.date).toLocaleString("pt");

      if (jsonObj.creationDate)
        this.creationDate = new Date(jsonObj.creationDate).toLocaleString("pt");

      if (jsonObj.availableDate)
        this.availableDate = new Date(jsonObj.availableDate).toLocaleString(
          "pt"
        );

      if (jsonObj.conclusionDate)
        this.conclusionDate = new Date(jsonObj.conclusionDate).toLocaleString(
          "pt"
        );

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
    }
  }
}
