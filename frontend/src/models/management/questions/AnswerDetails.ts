import QuestionDetails from '@/models/management/questions/QuestionDetails';

export default abstract class AnswerDetails {
  type!: string;

  constructor(type: string) {
    this.type = type;
  }

  abstract isCorrect(): boolean;

  abstract answerRepresentation(question: QuestionDetails): string;
}
