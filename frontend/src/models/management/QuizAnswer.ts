import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { ISOtoString } from '@/services/ConvertDateService';

export class QuizAnswer {
  id!: number;
  name!: number;
  username!: number;
  creationDate!: string;
  answerDate!: string;
  fraud: boolean = false;
  // When the student opened a single-session quiz, and how many times they tried to open it again
  sessionOpenedAt: string | null = null;
  blockedOpenings: number = 0;
  questionAnswers: QuestionAnswer[] = [];

  constructor(jsonObj?: QuizAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.answerDate = ISOtoString(jsonObj.answerDate);
      this.fraud = jsonObj.fraud;
      this.sessionOpenedAt = jsonObj.sessionOpenedAt ?? null;
      this.blockedOpenings = jsonObj.blockedOpenings ?? 0;

      if (jsonObj.questionAnswers) {
        this.questionAnswers = jsonObj.questionAnswers.map(
          (questionAnswer: QuestionAnswer) => new QuestionAnswer(questionAnswer)
        );
      }
    }
  }
}
