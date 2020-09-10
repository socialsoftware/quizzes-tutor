export default class DashboardInfo {
  name!: string;
  username!: string;
  numDiscussions!: number;
  numAnsweredDiscussions!: number;

  constructor(jsonObj?: DashboardInfo) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.numDiscussions = jsonObj.numDiscussions;
      this.numAnsweredDiscussions = jsonObj.numAnsweredDiscussions;
    }
  }
}
