import { QuizAnswer } from '@/models/management/QuizAnswer';

export class QuizAnswers {
  correctSequence!: number[];
  timeToSubmission!: number;
  quizAnswers: QuizAnswer[] = [];

  constructor(jsonObj?: QuizAnswers) {
    if (jsonObj) {
      this.correctSequence = jsonObj.correctSequence;
      this.timeToSubmission = jsonObj.timeToSubmission;

      this.quizAnswers = jsonObj.quizAnswers.map(
        (quizAnswer: QuizAnswer) => new QuizAnswer(quizAnswer)
      );
    }
  }
}
