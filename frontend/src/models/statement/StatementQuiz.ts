import StatementQuestion from "@/models/statement/StatementQuestion";

export default class StatementQuiz {
  courseName!: string;
  quizAnswerId!: number;
  title!: string;
  availableDate!: string;
  conclusionDate: string = "-";
  questions: StatementQuestion[] = [];

  constructor(jsonObj?: StatementQuiz) {
    if (jsonObj) {
      this.courseName = jsonObj.courseName;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.title = jsonObj.title;
      this.availableDate = jsonObj.availableDate;
      if (jsonObj.conclusionDate) {
        this.conclusionDate = jsonObj.conclusionDate;
      }

      this.questions = jsonObj.questions.map(question => {
        return new StatementQuestion(question);
      });
    }
  }
}
