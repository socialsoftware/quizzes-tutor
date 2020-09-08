import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { ISOtoString } from '@/services/ConvertDateService';
export class QuizAnswer {
    constructor(jsonObj) {
        this.questionAnswers = [];
        if (jsonObj) {
            this.name = jsonObj.name;
            this.username = jsonObj.username;
            this.creationDate = ISOtoString(jsonObj.creationDate);
            this.answerDate = ISOtoString(jsonObj.answerDate);
            if (jsonObj.questionAnswers) {
                this.questionAnswers = jsonObj.questionAnswers.map((questionAnswer) => new QuestionAnswer(questionAnswer));
            }
        }
    }
}
//# sourceMappingURL=QuizAnswer.js.map