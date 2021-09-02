export default class QuizFraudScore {
  username!: number;
  score!: number;

  constructor(jsonObj?: QuizFraudScore) {
    if (jsonObj) {
      this.username = jsonObj.username;
      this.score = jsonObj.score;
    }
  }
}
