export default class UserQuestionSubmissionInfo {
  userId: number | null = null;
  numQuestionSubmissions: number | null = null;
  username: string | null = null;
  name: string | null = null;

  constructor(jsonObj?: UserQuestionSubmissionInfo) {
    if (jsonObj) {
      this.userId = jsonObj.userId;
      this.numQuestionSubmissions = jsonObj.numQuestionSubmissions;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
    }
  }
}
