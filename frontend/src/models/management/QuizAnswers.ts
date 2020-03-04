import { QuizAnswer } from '@/models/management/QuizAnswer';

export class QuizAnswers {
  correctSequence!: number[];
  secondsToSubmission!: number;
  quizAnswers: QuizAnswer[] = [];

  constructor(jsonObj?: QuizAnswers) {
    if (jsonObj) {
      this.correctSequence = jsonObj.correctSequence;
      this.secondsToSubmission = jsonObj.secondsToSubmission;

      this.quizAnswers = jsonObj.quizAnswers.map(
        (quizAnswer: QuizAnswer) => new QuizAnswer(quizAnswer)
      );
    }
  }
}
