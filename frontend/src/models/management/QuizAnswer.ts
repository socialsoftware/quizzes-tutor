import { QuestionAnswer } from '@/models/management/QuestionAnswer';

export class QuizAnswer {
  name!: number;
  username!: number;
  creationDate!: string | undefined;
  answerDate!: string | undefined;
  questionAnswers: QuestionAnswer[] = [];

  constructor(jsonObj?: QuizAnswer) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.creationDate = jsonObj.creationDate;
      this.answerDate = jsonObj.answerDate;

      if (jsonObj.questionAnswers) {
        this.questionAnswers = jsonObj.questionAnswers.map(
          (questionAnswer: QuestionAnswer) => new QuestionAnswer(questionAnswer)
        );
      }
    }
  }
}
