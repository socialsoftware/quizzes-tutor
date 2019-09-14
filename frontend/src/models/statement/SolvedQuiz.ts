import StatementQuiz from "@/models/statement/StatementQuiz";
import StatementAnswer from "@/models/statement/StatementAnswer";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";

export default class SolvedQuiz {
  answerDate: string;
  statementQuiz: StatementQuiz;
  answers: StatementAnswer[] = [];
  correctAnswers: StatementCorrectAnswer[] = [];

  constructor(jsonObj: SolvedQuiz) {
    this.answerDate = new Date(jsonObj.answerDate).toLocaleString("pt");
    this.statementQuiz = jsonObj.statementQuiz;
    this.answers = jsonObj.answers;
    this.correctAnswers = jsonObj.correctAnswers;
  }
}
