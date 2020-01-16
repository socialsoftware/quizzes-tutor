export default class Course {
  id!: number;
  name!: string;
  acronym!: string;
  academicTerm!: string;

  constructor(jsonObj?: Course) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.acronym = jsonObj.acronym;
      this.academicTerm = jsonObj.academicTerm;
    }
  }
}
