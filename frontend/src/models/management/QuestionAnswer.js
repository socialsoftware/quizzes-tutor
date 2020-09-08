import Question from '@/models/management/Question';
import Option from '@/models/management/Option';
export class QuestionAnswer {
    constructor(jsonObj) {
        if (jsonObj) {
            this.question = new Question(jsonObj.question);
            this.option = new Option(jsonObj.option);
        }
    }
}
//# sourceMappingURL=QuestionAnswer.js.map