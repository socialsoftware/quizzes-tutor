import User from '@/models/user/User';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/user/Tournament';

// TODO: Is this needed? Probably can be deleted
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

  static sortByScore(a: TournamentScore, b: TournamentScore) {
    if (a && b) {
      return a.score < b.score ? 1 : -1;
    } else return 0;
  }

  static setRankings(tournamentScores: TournamentScore[]) {
    for (let i = 1; i <= tournamentScores.length; i++) {
      tournamentScores[i - 1].ranking = i;
    }
    return tournamentScores;
  }

  // TODO: Fix calculate score
  static calculateScore(quiz: SolvedQuiz, correctAnswers: number) {
    let correct = 0;
    for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
      if (
        quiz.statementQuiz.answers[i] &&
        quiz.statementQuiz.answers[i].isAnswerCorrect(quiz.correctAnswers[i])
      ) {
        correct += 1;
      }
    }
    correctAnswers = correct;
    return `${correct}/${quiz.statementQuiz.questions.length}`;
  }

  static async getScore(tournament: Tournament, correctAnswers: number) {
    const quizzes = await RemoteServices.getSolvedQuizzes();

    let score = '';
    quizzes.map((quiz) => {
      if (tournament && quiz.statementQuiz.id == tournament.quizId) {
        score = this.calculateScore(quiz, correctAnswers);
      }
    });
    return score;
  }

  static getPercentageColor(score: string) {
    const res = score.split('/');
    const percentage = (parseInt(res[0]) / parseInt(res[1])) * 100;
    if (percentage < 25) return 'red';
    else if (percentage < 50) return 'orange';
    else if (percentage < 75) return 'lime';
    else if (percentage <= 100) return 'green';
  }

  static getRankingColor(ranking: number) {
    if (ranking == 1) return '#d4Af37';
    else if (ranking == 2) return '#c0c0c0';
    else if (ranking == 3) return '#b9722d';
    else return 'primary';
  }
}
