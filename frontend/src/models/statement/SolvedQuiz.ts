import SolvedQuestion from "@/models/statement/SolvedQuestion";

export default class SolvedQuiz {
  title: string;
  answerDate: string;
  quizAnswerId: number;
  questions: SolvedQuestion[];

  constructor(jsonObj: SolvedQuiz) {
    this.title = jsonObj.title;
    this.answerDate = jsonObj.answerDate;
    this.quizAnswerId = jsonObj.quizAnswerId;
    this.questions = jsonObj.questions;
  }
}
