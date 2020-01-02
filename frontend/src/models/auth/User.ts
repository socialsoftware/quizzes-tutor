import Course from "@/models/auth/Course";

export default class User {
  name!: string;
  username!: string;
  role!: string;
  activeCourses: Course[] = [];
  otherCourses: Course[] = [];

  constructor(jsonObj?: User) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;

      this.activeCourses = jsonObj.activeCourses.map(
        (course: Course) => new Course(course)
      );

      this.otherCourses = jsonObj.otherCourses.map(
        (course: Course) => new Course(course)
      );
    }
  }
}
