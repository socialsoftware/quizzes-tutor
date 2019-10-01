import StatementQuestion from "@/models/statement/StatementQuestion";

export default class StatementQuiz {
  quizAnswerId!: number;
  title!: string;
  availableDate!: string;
  conclusionDate: string = "-";
  questions: StatementQuestion[] = [];

  constructor(jsonObj?: StatementQuiz) {
    if (jsonObj) {
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.title = jsonObj.title;
      this.availableDate = new Date(jsonObj.availableDate).toLocaleString("pt");
      if (jsonObj.conclusionDate) {
        this.conclusionDate = new Date(jsonObj.conclusionDate).toLocaleString(
          "pt"
        );
      }

      this.questions = jsonObj.questions.map(question => {
        return new StatementQuestion(question);
      });
    }
  }
}
