import { reactive } from 'vue';
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
  questionOrder: number = 0;
  timeToAvailability!: number | null;
  timeToSubmission!: number | null;
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];
  lastTimeCalled: number = Date.now();
  timerId!: number;

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
      this.questionOrder = jsonObj.questionOrder;

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
        // The pages keep the quiz in a ref, which watches a reactive proxy of this object. `this` is
        // the plain object, so changes made through it are never seen: the clock stayed still and
        // the watcher that concludes the quiz at zero never ran. The proxy of an object is always
        // the same one, so going through it lets the pages see every second.
        const self = reactive(this) as StatementQuiz;
        this.timerId = window.setInterval(() => {
          const elapsed = Math.floor(Date.now() - self.lastTimeCalled);

          if (self.timeToAvailability != null && self.timeToAvailability > 0) {
            self.timeToAvailability = Math.max(0, self.timeToAvailability - elapsed);
          }

          if (self.timeToSubmission != null && self.timeToSubmission > 0) {
            self.timeToSubmission = Math.max(0, self.timeToSubmission - elapsed);
          }

          if (!self.timeToSubmission && !self.timeToAvailability) {
            clearInterval(self.timerId);
          }

          self.lastTimeCalled = Date.now();
        }, 1000);
      }
    }
  }

  unansweredQuestions(): number {
    return this.answers.filter((x) => !x.isQuestionAnswered()).length;
  }
}
