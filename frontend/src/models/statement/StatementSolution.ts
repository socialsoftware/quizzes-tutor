import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";

export default class StatementSolution {
  answers: StatementCorrectAnswer[] = [];

  constructor(jsonObj: StatementSolution) {
    jsonObj.answers.forEach(answer => {
      this.answers.push(new StatementCorrectAnswer(answer));
    });
  }
}
