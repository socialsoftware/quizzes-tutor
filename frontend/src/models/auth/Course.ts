import CourseExecution from "@/models/auth/CourseExecution";

export default class Course {
  name!: string;
  executions: CourseExecution[] = [];

  constructor(jsonObj?: Course) {
    if (jsonObj) {
      // this.name = jsonObj.name;
      console.log({ jsonObj });
    }
  }
}
