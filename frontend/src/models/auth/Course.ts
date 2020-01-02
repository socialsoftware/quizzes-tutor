export default class Course {
  name!: string;
  acronym!: string;
  academicTerm!: string;

  constructor(jsonObj?: Course) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.acronym = jsonObj.acronym;
      this.academicTerm = jsonObj.academicTerm;
    }
  }
}
