import QuizFraudScore from "@/models/management/QuizFraudScore";

export default class QuizFraudScores {
  fraudScores: QuizFraudScore[] = [];

  constructor(jsonObj?: QuizFraudScores) {
    if (jsonObj) {
      this.fraudScores = jsonObj.fraudScores.map(fraudScore => new QuizFraudScore(fraudScore));
    }
  }
}
