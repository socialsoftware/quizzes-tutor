import StatementQuestion from "@/models/statement/StatementQuestion";

export default class StatementQuiz {
  quizAnswerId: number | null = null;
  questions: StatementQuestion[] = [];
}
