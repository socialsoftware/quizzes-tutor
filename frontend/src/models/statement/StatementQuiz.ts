import StatementQuestion from "@/models/statement/StatementQuestion";

export default class StatementQuiz {
  quizAnswerId: number;
  title: string;
  availableDate: string;
  conclusionDate: string;
  questions: StatementQuestion[] = [];

  constructor(jsonObj: StatementQuiz) {
    this.quizAnswerId = jsonObj.quizAnswerId;
    this.title = jsonObj.title;
    this.availableDate = new Date(jsonObj.availableDate).toLocaleString("pt");
    this.conclusionDate = new Date(jsonObj.conclusionDate).toLocaleString("pt");
    jsonObj.questions.forEach(question => {
      this.questions.push(new StatementQuestion(question));
    });
  }
}
