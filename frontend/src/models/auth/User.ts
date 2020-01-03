import CourseExecution from "@/models/auth/CourseExecution";

export default class User {
  name!: string;
  username!: string;
  role!: string;
  courses: Map<string, CourseExecution[]> = new Map<
    string,
    CourseExecution[]
  >();

  constructor(jsonObj?: User) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
      this.courses = jsonObj.courses;
    }
  }
}
