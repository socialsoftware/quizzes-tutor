import StatementQuestion from "@/models/statement/StatementQuestion";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import StatementAnswer from "@/models/statement/StatementAnswer";
import RemoteServices from "@/services/RemoteServices";
import StatementQuiz from "@/models/statement/StatementQuiz";

export default class StatementManager {
  topic: string[] = ["1"];
  questionType: string = "new";
  numberOfQuestions: string = "5";
  statementQuiz: StatementQuiz | null = null;
  answers: StatementAnswer[] = [];
  correctAnswers: StatementCorrectAnswer[] = [];

  private static _quiz: StatementManager = new StatementManager();

  static get getInstance(): StatementManager {
    return this._quiz;
  }

  async getQuizStatement() {
    let params = {
      topic: this.topic,
      questionType: this.questionType,
      numberOfQuestions: +this.numberOfQuestions
    };

    this.statementQuiz = await RemoteServices.getQuizStatement(params);

    this.answers = this.statementQuiz.questions.map(
      (question: StatementQuestion) =>
        new StatementAnswer(question.quizQuestionId)
    );
  }

  async getCorrectAnswers() {
    if (this.statementQuiz) {
      let params = {
        answerDate: new Date().toISOString(),
        quizAnswerId: this.statementQuiz.quizAnswerId,
        answers: this.answers
      };

      this.correctAnswers = await RemoteServices.getCorrectAnswers(params);
    } else {
      throw Error("No quiz");
    }
  }

  reset() {
    this.statementQuiz = null;
    this.answers = [];
    this.correctAnswers = [];
  }

  isEmpty(): boolean {
    return this.statementQuiz == null;
  }
}
