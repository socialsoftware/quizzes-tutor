import StatementQuestion from "@/models/statement/StatementQuestion";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import StatementAnswer from "@/models/statement/StatementAnswer";
import RemoteServices from "@/services/RemoteServices";
import StatementQuiz from "@/models/statement/StatementQuiz";

export default class StatementManager {
  questionType: string = "all";
  assessment: string = "all";
  // topic: string[] = [];
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
      // topic: this.topic,
      questionType: this.questionType,
      assessment: this.assessment,
      numberOfQuestions: +this.numberOfQuestions
    };

    this.statementQuiz = await RemoteServices.getQuizStatement(params);

    this.answers = this.statementQuiz.questions.map(
      (question: StatementQuestion) => {
        let answer = new StatementAnswer();
        answer.quizQuestionId = question.quizQuestionId;
        return answer;
      }
    );
  }

  async getCorrectAnswers() {
    if (this.statementQuiz) {
      let params = {
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
