export class FraudScore {
  username!: string;
  score!: number;

  constructor(jsonObj?: FraudScore) {
    if (jsonObj) {
      this.username = jsonObj.username;
      if (jsonObj.score) this.score = jsonObj.score;
    }
  }
}
