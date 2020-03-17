import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';

export default class StatementQuiz {
  id!: number;
  courseName!: string;
  quizAnswerId!: number;
  title!: string;
  qrCodeOnly!: boolean;
  oneWay!: boolean;
  availableDate!: string;
  conclusionDate: string = '-';
  secondsToAvailability!: number;
  secondsToSubmission!: number;
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];

  constructor(jsonObj?: StatementQuiz) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseName = jsonObj.courseName;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.title = jsonObj.title;
      this.qrCodeOnly = jsonObj.qrCodeOnly;
      this.oneWay = jsonObj.oneWay;
      this.availableDate = jsonObj.availableDate;
      this.secondsToAvailability = jsonObj.secondsToAvailability;
      if (jsonObj.conclusionDate) {
        this.conclusionDate = jsonObj.conclusionDate;
        this.secondsToSubmission = jsonObj.secondsToSubmission;
      }

      this.questions = jsonObj.questions.map(question => {
        return new StatementQuestion(question);
      });

      if (jsonObj.answers) {
        this.answers = jsonObj.answers.map(answer => {
          return new StatementAnswer(answer);
        });
      }
    }
  }
}
