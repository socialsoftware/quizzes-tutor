export default class DashboardInfo {
  name!: string;
  username!: string;
  discussionsInfoPublic!: boolean;
  numDiscussions!: number;

  constructor(jsonObj?: DashboardInfo) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.discussionsInfoPublic = jsonObj.discussionsInfoPublic;
      this.numDiscussions = jsonObj.numDiscussions;
    }
  }
}
