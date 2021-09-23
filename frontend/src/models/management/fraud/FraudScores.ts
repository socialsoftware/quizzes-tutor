import { FraudScore } from '@/models/management/fraud/FraudScore';

export class FraudScores {
  fraudScores: FraudScore[] = [];

  constructor(jsonObj?: FraudScore[]) {
    if (jsonObj) {
      this.fraudScores = jsonObj.map(
        (fraudScore) => new FraudScore(fraudScore)
      );
    }
  }
}
