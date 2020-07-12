import User from '@/models/user/User';

export default class TournamentScore {
  username: string;
  name: string;
  score: string;
  correctAnswers: number;
  ranking: number;

  constructor(user: User, score: string, correctAnswers: number) {
    this.username = user.username;
    this.name = user.name;
    this.score = score;
    this.correctAnswers = correctAnswers;
    this.ranking = 0;
  }
}
