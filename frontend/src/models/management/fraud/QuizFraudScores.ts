import QuizFraudScore from '@/models/management/fraud/QuizFraudScore';

export default class QuizFraudScores {
  fraudScores: QuizFraudScore[] = [];

  constructor(jsonObj?: QuizFraudScore[]) {
    if (jsonObj) {
      this.fraudScores = jsonObj.map(
        (fraudScore) => new QuizFraudScore(fraudScore)
      );
    }
  }
}
