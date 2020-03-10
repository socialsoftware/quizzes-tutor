export default class Course {
  courseId!: number;
  courseType!: string;
  name!: string;
  courseExecutionId!: number;
  courseExecutionType!: string;
  acronym!: string;
  academicTerm!: string;
  status!: string;

  constructor(jsonObj?: Course) {
    if (jsonObj) {
      this.courseId = jsonObj.courseId;
      this.courseType = jsonObj.courseType;
      this.name = jsonObj.name;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.courseExecutionType = jsonObj.courseExecutionType;
      this.acronym = jsonObj.acronym;
      this.academicTerm = jsonObj.academicTerm;
      this.status = jsonObj.status;
    }
  }
}
