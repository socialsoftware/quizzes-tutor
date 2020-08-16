import QuestionSubmission from '@/models/management/QuestionSubmission';

export default class UserQuestionSubmissionInfo {
  userId: number | null = null;
  questionSubmissions: QuestionSubmission[] = [];
  numQuestionSubmissions: number | null = null;
  username: string | null = null;
  name: string | null = null;

  constructor(jsonObj?: UserQuestionSubmissionInfo) {
    if (jsonObj) {
      this.userId = jsonObj.userId;
      this.numQuestionSubmissions = jsonObj.numQuestionSubmissions;
      this.name = jsonObj.name;
      this.username = jsonObj.username;

      this.questionSubmissions = jsonObj.questionSubmissions.map((questionSubmission: QuestionSubmission) => new QuestionSubmission(questionSubmission));
    }
  }

  hasNoSubmissions() {
    return this.questionSubmissions.length === 0;
  }
}
