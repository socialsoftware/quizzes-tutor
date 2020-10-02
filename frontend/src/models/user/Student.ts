import User from '@/models/user/User';

export class Student extends User {
  number!: number;
  numberOfTeacherQuizzes!: number;
  numberOfStudentQuizzes!: number;
  numberOfAnswers!: number;
  numberOfTeacherAnswers!: number;
  percentageOfCorrectAnswers!: number;
  percentageOfCorrectTeacherAnswers!: number;

  constructor(jsonObj?: Student) {
    super(jsonObj);
    if (jsonObj) {
      this.number = jsonObj.number;
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
