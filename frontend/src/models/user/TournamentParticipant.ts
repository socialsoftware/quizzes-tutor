export default class TournamentParticipant {
  userId!: number;
  name!: string;
  username!: string;
  answered!: boolean;
  score!: number;
  numberOfAnswered!: number;
  numberOfCorrect!: number;

  constructor(jsonObj?: TournamentParticipant) {
    if (jsonObj) {
      this.userId = jsonObj.userId;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.answered = jsonObj.answered;
      this.score = jsonObj.score;
      this.numberOfAnswered = jsonObj.numberOfAnswered;
      this.numberOfCorrect = jsonObj.numberOfCorrect;
    }
  }
}
