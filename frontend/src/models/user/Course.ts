import User from './User';

export default class Course {
  academicTerm: string | undefined;
  acronym: string | undefined;
  courseExecutionId: number | undefined;
  courseExecutionType: string = 'EXTERNAL';
  courseId: number | undefined;
  courseType: string = 'EXTERNAL';
  name: string | undefined;
  endDate: string | undefined;
  numberOfQuestions: number | undefined;
  numberOfQuizzes: number | undefined;
  numberOfActiveStudents: number | undefined;
  numberOfInactiveStudents: number | undefined;
  numberOfActiveTeachers: number | undefined;
  numberOfInactiveTeachers: number | undefined;
  status: string | undefined;
  courseExecutionUsers: User[] = [];

  constructor(jsonObj?: Course) {
    if (jsonObj) {
      this.academicTerm = jsonObj.academicTerm;
      this.acronym = jsonObj.acronym;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.courseExecutionType = jsonObj.courseExecutionType;
      this.courseId = jsonObj.courseId;
      this.courseType = jsonObj.courseType;
      this.name = jsonObj.name;
      this.endDate = jsonObj.endDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfQuizzes = jsonObj.numberOfQuizzes;
      this.numberOfActiveStudents = jsonObj.numberOfActiveStudents;
      this.numberOfInactiveStudents = jsonObj.numberOfInactiveStudents;
      this.numberOfActiveTeachers = jsonObj.numberOfActiveTeachers;
      this.numberOfInactiveTeachers = jsonObj.numberOfInactiveTeachers;
      this.status = jsonObj.status;
      if (jsonObj.courseExecutionUsers) {
        this.courseExecutionUsers = jsonObj.courseExecutionUsers.map(
          (user: User) => new User(user)
        );
      }
    }
  }
}
