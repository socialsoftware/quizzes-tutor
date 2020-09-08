import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import { ISOtoString } from '@/services/ConvertDateService';
export default class SolvedQuiz {
    constructor(jsonObj) {
        this.correctAnswers = [];
        if (jsonObj) {
            this.answerDate = ISOtoString(jsonObj.answerDate);
            this.statementQuiz = new StatementQuiz(jsonObj.statementQuiz);
            this.correctAnswers = jsonObj.correctAnswers.map(correctAnswer => {
                return new StatementCorrectAnswer(correctAnswer);
            });
        }
    }
}
//# sourceMappingURL=SolvedQuiz.js.map