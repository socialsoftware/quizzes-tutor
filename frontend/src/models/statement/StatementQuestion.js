import StatementOption from '@/models/statement/StatementOption';
import { _ } from 'vue-underscore';
export default class StatementQuestion {
    constructor(jsonObj) {
        this.image = null;
        this.options = [];
        if (jsonObj) {
            this.quizQuestionId = jsonObj.quizQuestionId;
            this.content = jsonObj.content;
            this.image = jsonObj.image;
            if (jsonObj.options) {
                this.options = _.shuffle(jsonObj.options.map((option) => new StatementOption(option)));
            }
        }
    }
}
//# sourceMappingURL=StatementQuestion.js.map