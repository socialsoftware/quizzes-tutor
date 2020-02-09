export default class Course {
  courseId!: number;
  name!: string;
  courseExecutionId!: number;
  acronym!: string;
  academicTerm!: string;
  status!: string;

  constructor(jsonObj?: Course) {
    if (jsonObj) {
      this.courseId = jsonObj.courseId;
      this.name = jsonObj.name;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.acronym = jsonObj.acronym;
      this.academicTerm = jsonObj.academicTerm;
      this.status = jsonObj.status;
    }
  }
}
