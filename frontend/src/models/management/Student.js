export class Student {
    constructor(jsonObj) {
        this.username = null;
        this.name = null;
        if (jsonObj) {
            this.number = jsonObj.number;
            this.username = jsonObj.username;
            this.name = jsonObj.name;
            this.numberOfTeacherQuizzes = jsonObj.numberOfTeacherQuizzes;
            this.numberOfStudentQuizzes = jsonObj.numberOfStudentQuizzes;
            this.numberOfAnswers = jsonObj.numberOfAnswers;
            this.numberOfTeacherAnswers = jsonObj.numberOfTeacherAnswers;
            this.percentageOfCorrectAnswers = jsonObj.percentageOfCorrectAnswers;
            this.percentageOfCorrectTeacherAnswers =
                jsonObj.percentageOfCorrectTeacherAnswers;
        }
    }
}
//# sourceMappingURL=Student.js.map