import AuthUser from '@/models/user/AuthUser';

export class Student extends AuthUser{
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