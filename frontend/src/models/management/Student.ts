export class Student {
  number!: number;
  username: string | null = null;
  name: string | null = null;
  year!: number;
  numberOfTeacherQuizzes!: number;
  numberOfStudentQuizzes!: number;
  numberOfAnswers!: number;
  percentageOfCorrectAnswers!: number;
  numberOfTeacherAnswers!: number;
  percentageOfCorrectTeacherAnswers!: number;

  constructor(jsonObj?: Student) {
    if (jsonObj) {
      this.number = jsonObj.number;
      this.username = jsonObj.username;
      this.name = jsonObj.name;
      this.year = jsonObj.year;
      this.numberOfTeacherQuizzes = jsonObj.numberOfTeacherQuizzes;
      this.numberOfStudentQuizzes = jsonObj.numberOfStudentQuizzes;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.percentageOfCorrectAnswers = jsonObj.percentageOfCorrectAnswers;
      this.numberOfTeacherAnswers = jsonObj.numberOfTeacherAnswers;
      this.percentageOfCorrectTeacherAnswers =
        jsonObj.percentageOfCorrectTeacherAnswers;
    }
  }
}
