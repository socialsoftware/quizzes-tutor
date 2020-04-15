import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { ISOtoString } from '@/services/ConvertDateService';

export class QuizAnswer {
  name!: number;
  username!: number;
  creationDate!: string;
  answerDate!: string;
  questionAnswers: QuestionAnswer[] = [];

  constructor(jsonObj?: QuizAnswer) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.answerDate = ISOtoString(jsonObj.answerDate);

      if (jsonObj.questionAnswers) {
        this.questionAnswers = jsonObj.questionAnswers.map(
          (questionAnswer: QuestionAnswer) => new QuestionAnswer(questionAnswer)
        );
      }
    }
  }
}
