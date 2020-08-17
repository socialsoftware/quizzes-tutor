export default class StatementAnswer {
    constructor(jsonObj) {
        this.optionId = null;
        this.timeTaken = 0;
        this.timeToSubmission = null;
        if (jsonObj) {
            this.optionId = jsonObj.optionId;
            this.timeTaken = jsonObj.timeTaken;
            this.sequence = jsonObj.sequence;
            this.questionAnswerId = jsonObj.questionAnswerId;
            this.quizQuestionId = jsonObj.quizQuestionId;
        }
    }
}
//# sourceMappingURL=StatementAnswer.js.map