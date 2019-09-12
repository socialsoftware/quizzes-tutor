import StatementQuestion from "@/models/statement/StatementQuestion";

export default class StatementQuiz {
  quizAnswerId: number;
  title: string;
  availableDate: String;
  conclusionDate: String;
  questions: StatementQuestion[] = [];

  constructor(jsonObj: StatementQuiz) {
    this.quizAnswerId = jsonObj.quizAnswerId;
    this.title = jsonObj.title;
    this.availableDate = jsonObj.availableDate;
    this.conclusionDate = jsonObj.conclusionDate;
    jsonObj.questions.forEach(question => {
      this.questions.push(new StatementQuestion(question));
    });
  }
}
