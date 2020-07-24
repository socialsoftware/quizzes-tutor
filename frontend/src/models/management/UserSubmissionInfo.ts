export default class UserSubmissionInfo {
  userId: number | null = null;
  numSubmissions: number | null = null;
  username: string | null = null;
  name: string | null = null;

  constructor(jsonObj?: UserSubmissionInfo) {
    if (jsonObj) {
      this.userId = jsonObj.userId;
      this.numSubmissions = jsonObj.numSubmissions;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
    }
  }
}
