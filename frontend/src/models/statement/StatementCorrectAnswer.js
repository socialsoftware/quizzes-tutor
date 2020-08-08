export default class StatementCorrectAnswer {
    constructor(jsonObj) {
        if (jsonObj) {
            this.quizQuestionId = jsonObj.quizQuestionId;
            this.correctOptionId = jsonObj.correctOptionId;
            this.sequence = jsonObj.sequence;
        }
    }
}
//# sourceMappingURL=StatementCorrectAnswer.js.map