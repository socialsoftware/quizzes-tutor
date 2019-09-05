import StatementQuestion from "@/models/statement/StatementQuestion";

export default class StatementQuiz {
  quizAnswerId: number | null = null;
  questions: StatementQuestion[] = [];

  constructor(jsonObj: StatementQuiz) {
    this.quizAnswerId = jsonObj.quizAnswerId;
    jsonObj.questions.forEach(question => {
      this.questions.push(new StatementQuestion(question));
    });
  }
}
