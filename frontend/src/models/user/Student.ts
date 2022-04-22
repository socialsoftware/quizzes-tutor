import User from '@/models/user/User';

export class Student extends User {
  number!: number;
  numberOfTeacherQuizzes!: number;
  numberOfInClassQuizzes!: number;
  numberOfStudentQuizzes!: number;
  numberOfTeacherAnswers!: number;
  numberOfInClassAnswers!: number;
  numberOfStudentAnswers!: number;
  numberOfAnswers!: number;
  percentageOfCorrectTeacherAnswers!: number;
  percentageOfCorrectInClassAnswers!: number;
  percentageOfCorrectStudentAnswers!: number;
  percentageOfCorrectAnswers!: number;

  constructor(jsonObj?: Student) {
    super(jsonObj);
    if (jsonObj) {
      this.number = jsonObj.number;
      this.numberOfTeacherQuizzes = jsonObj.numberOfTeacherQuizzes;
      this.numberOfInClassQuizzes = jsonObj.numberOfInClassQuizzes;
      this.numberOfStudentQuizzes = jsonObj.numberOfStudentQuizzes;
      this.numberOfTeacherAnswers = jsonObj.numberOfTeacherAnswers;
      this.numberOfInClassAnswers = jsonObj.numberOfInClassAnswers;
      this.numberOfStudentAnswers = jsonObj.numberOfStudentAnswers;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.percentageOfCorrectTeacherAnswers =
        jsonObj.percentageOfCorrectTeacherAnswers;
      this.percentageOfCorrectInClassAnswers =
        jsonObj.percentageOfCorrectInClassAnswers;
      this.percentageOfCorrectStudentAnswers =
        jsonObj.percentageOfCorrectStudentAnswers;
      this.percentageOfCorrectAnswers = jsonObj.percentageOfCorrectAnswers;
    }
  }
}
