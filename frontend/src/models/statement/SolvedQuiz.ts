import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

export default class SolvedQuiz {
  answerDate!: string;
  statementQuiz!: StatementQuiz;
  correctAnswers: StatementCorrectAnswer[] = [];

  constructor(jsonObj?: SolvedQuiz) {
    if (jsonObj) {
      this.answerDate = jsonObj.answerDate;
      this.statementQuiz = new StatementQuiz(jsonObj.statementQuiz);

      this.correctAnswers = jsonObj.correctAnswers.map(correctAnswer => {
        return new StatementCorrectAnswer(correctAnswer);
      });
    }
  }
}
