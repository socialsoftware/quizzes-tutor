import { reactive } from 'vue';
import { QuizAnswer } from '@/models/management/QuizAnswer';

export class QuizAnswers {
  correctSequence!: string[];
  timeToSubmission!: number;
  quizAnswers: QuizAnswer[] = [];
  lastTimeCalled: number = Date.now();
  timerId!: number;

  constructor(jsonObj?: QuizAnswers) {
    if (jsonObj) {
      this.correctSequence = jsonObj.correctSequence;
      this.timeToSubmission = jsonObj.timeToSubmission;

      this.quizAnswers = jsonObj.quizAnswers.map(
        (quizAnswer: QuizAnswer) => new QuizAnswer(quizAnswer)
      );
    }

    // if there is timeToSubmission start an interval that decreases the timeToSubmission every second
    if (this.timeToSubmission != null && this.timeToSubmission > 0) {
      // through the reactive proxy, or the page never sees the clock move (see StatementQuiz)
      const self = reactive(this) as QuizAnswers;
      this.timerId = window.setInterval(() => {
        if (self.timeToSubmission != null && self.timeToSubmission > 0) {
          self.timeToSubmission = Math.max(
            0,
            self.timeToSubmission - Math.floor(Date.now() - self.lastTimeCalled)
          );
        }

        if (!self.timeToSubmission) {
          clearInterval(self.timerId);
        }

        self.lastTimeCalled = Date.now();
      }, 1000);
    }
  }
}
