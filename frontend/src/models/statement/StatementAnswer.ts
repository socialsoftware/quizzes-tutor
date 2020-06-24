import StatementCorrectAnswer from './StatementCorrectAnswer';

export default abstract class StatementAnswer {
  public timeTaken: number = 0;
  public sequence!: number;
  public questionAnswerId!: number;
  public type: string = "multiple_choice"
  public quizQuestionId!: number;
  public timeToSubmission: number | null = null;

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.type = jsonObj.type;
      this.quizQuestionId = jsonObj.quizQuestionId;
    }
  }

  abstract isAnswered(): boolean;
  abstract isCorrect(correctAnswer : StatementCorrectAnswer): boolean;
}
