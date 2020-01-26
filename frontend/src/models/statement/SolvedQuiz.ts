import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

export default class SolvedQuiz {
  answerDate!: string;
  statementQuiz!: StatementQuiz;
  answers: StatementAnswer[] = [];
  correctAnswers: StatementCorrectAnswer[] = [];

  constructor(jsonObj?: SolvedQuiz) {
    if (jsonObj) {
      this.answerDate = jsonObj.answerDate;
      this.statementQuiz = new StatementQuiz(jsonObj.statementQuiz);

      this.answers = jsonObj.answers.map(answer => {
        return new StatementAnswer(answer);
      });

      this.correctAnswers = jsonObj.correctAnswers.map(correctAnswer => {
        return new StatementCorrectAnswer(correctAnswer);
      });
    }
  }
}
