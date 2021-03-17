import Course from '@/models/user/Course';

interface CourseMap {
  [key: string]: Course[];
}

export default class AuthUser {
  id: number | null = null;
  name!: string;
  username!: string;
  role!: string;
  admin!: boolean;
  courses: CourseMap = {};
  coursesNumber: number = 0;

  constructor(jsonObj?: AuthUser) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
      this.admin = jsonObj.admin;

      for (const [term, courses] of Object.entries(jsonObj.courses)) {
        this.courses[term] = courses.map((course) => new Course(course));
        this.coursesNumber += this.courses[term].length;
      }
    }
  }
}
