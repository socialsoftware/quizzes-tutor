import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import { ISOtoString } from '@/services/ConvertDateService';
import { QuestionFactory } from '@/services/QuestionFactory';

export default class SolvedQuiz {
  answerDate!: string;
  statementQuiz!: StatementQuiz;
  correctAnswers: StatementCorrectAnswer[] = [];

  constructor(jsonObj?: SolvedQuiz) {
    if (jsonObj) {
      this.answerDate = ISOtoString(jsonObj.answerDate);
      this.statementQuiz = new StatementQuiz(jsonObj.statementQuiz);

      this.correctAnswers = jsonObj.correctAnswers.map(correctAnswer => {
        return QuestionFactory.createCorrectAnswer(correctAnswer);
      });
    }
  }
}
