import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { ISOtoString } from '@/services/ConvertDateService';

export default class StatementQuiz {
  id!: number;
  courseName!: string;
  quizAnswerId!: number;
  title!: string;
  qrCodeOnly!: boolean;
  oneWay!: boolean;
  timed!: boolean;
  availableDate!: string;
  conclusionDate!: string;
  timeToAvailability!: number | null;
  timeToSubmission!: number | null;
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];
  private lastTimeCalled: number = Date.now();
  private timerId!: number;

  constructor(jsonObj?: StatementQuiz) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseName = jsonObj.courseName;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.title = jsonObj.title;
      this.qrCodeOnly = jsonObj.qrCodeOnly;
      this.oneWay = jsonObj.oneWay;
      this.timed = jsonObj.timed;
      this.availableDate = ISOtoString(jsonObj.availableDate);
      this.conclusionDate = ISOtoString(jsonObj.conclusionDate);

      this.timeToAvailability = jsonObj.timeToAvailability;
      this.timeToSubmission = jsonObj.timeToSubmission;

      this.questions = jsonObj.questions.map((question) => {
        return new StatementQuestion(question);
      });

      if (jsonObj.answers) {
        this.answers = jsonObj.answers.map((answer) => {
          return new StatementAnswer(answer);
        });
      }

      // if there is timeTo... start an interval that decreases the timeTo... every second
      if (
        (this.timeToSubmission != null && this.timeToSubmission > 0) ||
        (this.timeToAvailability != null && this.timeToAvailability > 0)
      ) {
        this.timerId = window.setInterval(() => {
          if (this.timeToAvailability != null && this.timeToAvailability > 0) {
            this.timeToAvailability = Math.max(
              0,
              this.timeToAvailability -
                Math.floor(Date.now() - this.lastTimeCalled)
            );
          }

          if (this.timeToSubmission != null && this.timeToSubmission > 0) {
            this.timeToSubmission = Math.max(
              0,
              this.timeToSubmission -
                Math.floor(Date.now() - this.lastTimeCalled)
            );
          }

          if (!this.timeToSubmission && !this.timeToAvailability) {
            clearInterval(this.timerId);
          }

          this.lastTimeCalled = Date.now();
        }, 1000);
      }
    }
  }

  unansweredQuestions(): number {
    return this.answers.filter((x) => !x.isQuestionAnswered()).length;
  }
}
