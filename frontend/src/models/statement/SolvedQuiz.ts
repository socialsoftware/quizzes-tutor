import StatementQuiz from "@/models/statement/StatementQuiz";
import StatementAnswer from "@/models/statement/StatementAnswer";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import StatementQuestion from "@/models/statement/StatementQuestion";

export default class SolvedQuiz {
  answerDate!: string;
  statementQuiz!: StatementQuiz;
  answers: StatementAnswer[] = [];
  correctAnswers: StatementCorrectAnswer[] = [];

  constructor(jsonObj?: SolvedQuiz) {
    if (jsonObj) {
      this.answerDate = new Date(jsonObj.answerDate).toLocaleString("pt");
      this.statementQuiz = new StatementQuiz(jsonObj.statementQuiz);

      jsonObj.answers.forEach(answer => {
        this.answers.push(new StatementAnswer(answer));
      });

      jsonObj.correctAnswers.forEach(correctAnswer => {
        this.correctAnswers.push(new StatementCorrectAnswer(correctAnswer));
      });
    }
  }
}
