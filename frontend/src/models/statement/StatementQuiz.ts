import StatementQuestion from "@/models/statement/StatementQuestion";
import StatementQuiz from "@/models/statement/StatementQuiz";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import StatementAnswer from "@/models/statement/StatementAnswer";
import RemoteServices from "@/services/RemoteServices";
import { Store } from "vuex";

export default class StudentQuiz {
  id: number | null = null;
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];
  correctAnswers: StatementCorrectAnswer[] = [];

  topic: string[] = ["1"];
  questionType: string = "new";
  numberOfQuestions: string = "5";

  private static _quiz: StatementQuiz = new StatementQuiz();

  static get getInstance(): StatementQuiz {
    return this._quiz;
  }

  async getQuestions() {
    let params = {
      topic: this.topic,
      questionType: this.questionType,
      numberOfQuestions: +this.numberOfQuestions
    };

    const quizAnswer = await RemoteServices.getQuizAnswer(params);

    this.id = quizAnswer.quizAnswerId;
    this.questions = quizAnswer.questions;
    this.answers = quizAnswer.questions.map(
      (question: StatementQuestion) =>
        new StatementAnswer(question.quizQuestionId)
    );
  }

  async getCorrectAnswers(): Promise<StatementCorrectAnswer[]> {
    let params = {
      answerDate: new Date().toISOString(),
      quizAnswerId: this.id,
      answers: this.answers
    };

    return await RemoteServices.getCorrectAnswers(params);
  }

  reset() {
    this.id = null;
    this.questions = [];
    this.answers = [];
    this.correctAnswers = [];
  }

  isEmpty(): boolean {
    return this.id === undefined || this.questions.length === 0;
  }
}
