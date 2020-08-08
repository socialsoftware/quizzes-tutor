import Course from '@/models/user/Course';
export default class User {
    constructor(jsonObj) {
        this.courses = {};
        this.coursesNumber = 0;
        if (jsonObj) {
            this.name = jsonObj.name;
            this.username = jsonObj.username;
            this.role = jsonObj.role;
            this.admin = jsonObj.admin;
            for (let [name, courses] of Object.entries(jsonObj.courses)) {
                this.courses[name] = courses.map(course => new Course(course));
                this.coursesNumber += this.courses[name].length;
            }
        }
    }
}
//# sourceMappingURL=User.js.map