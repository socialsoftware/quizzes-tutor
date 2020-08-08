export default class StudentStats {
    constructor(jsonObj) {
        if (jsonObj) {
            this.totalQuizzes = jsonObj.totalQuizzes;
            this.totalAnswers = jsonObj.totalAnswers;
            this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
            this.correctAnswers = jsonObj.correctAnswers;
            this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
            this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
            this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
            this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
        }
    }
}
//# sourceMappingURL=StudentStats.js.map