export default class Course {
  courseId: number | undefined;
  courseType: string = 'EXTERNAL';
  name: string | undefined;
  courseExecutionId: number | undefined;
  courseExecutionType: string = 'EXTERNAL';
  acronym: string | undefined;
  academicTerm: string | undefined;
  status: string | undefined;

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
