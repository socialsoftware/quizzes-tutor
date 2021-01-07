import { QuizAnswer } from '@/models/management/QuizAnswer';

export class QuizAnswers {
  correctSequence!: string[];
  timeToSubmission!: number;
  quizAnswers: QuizAnswer[] = [];
  private lastTimeCalled: number = Date.now();
  private timerId!: number;

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
      this.timerId = window.setInterval(() => {
        if (this.timeToSubmission != null && this.timeToSubmission > 0) {
          this.timeToSubmission = Math.max(
            0,
            this.timeToSubmission - Math.floor(Date.now() - this.lastTimeCalled)
          );
        }

        if (!this.timeToSubmission) {
          clearInterval(this.timerId);
        }

        this.lastTimeCalled = Date.now();
      }, 1000);
    }
  }
}
