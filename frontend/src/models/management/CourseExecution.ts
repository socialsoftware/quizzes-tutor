export class CourseExecution {
  year!: number;

  constructor(jsonObj?: CourseExecution) {
    if (jsonObj) {
      this.year = jsonObj.year;
    }
  }
}
