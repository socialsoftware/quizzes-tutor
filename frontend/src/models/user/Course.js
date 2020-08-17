export default class Course {
    constructor(jsonObj) {
        this.courseExecutionType = 'EXTERNAL';
        this.courseType = 'EXTERNAL';
        if (jsonObj) {
            this.academicTerm = jsonObj.academicTerm;
            this.acronym = jsonObj.acronym;
            this.courseExecutionId = jsonObj.courseExecutionId;
            this.courseExecutionType = jsonObj.courseExecutionType;
            this.courseId = jsonObj.courseId;
            this.courseType = jsonObj.courseType;
            this.name = jsonObj.name;
            this.numberOfQuestions = jsonObj.numberOfQuestions;
            this.numberOfQuizzes = jsonObj.numberOfQuizzes;
            this.numberOfStudents = jsonObj.numberOfStudents;
            this.numberOfTeachers = jsonObj.numberOfTeachers;
            this.status = jsonObj.status;
        }
    }
}
//# sourceMappingURL=Course.js.map