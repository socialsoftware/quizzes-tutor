import Course from "@/models/auth/Course";

export default class User {
  name!: string;
  username!: string;
  role!: string;
  courses: Map<string, Course[]> = new Map<string, Course[]>();

  constructor(jsonObj?: User) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
      this.courses = jsonObj.courses;
    }
  }
}
