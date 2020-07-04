import Question from '@/models/management/Question';

export default class Submission {
  id!: number;
  courseExecutionId!: number;
  question!: Question;
  userId: number | null = null;
  username: string | null = null;
  anonymous!: boolean;

  constructor(jsonObj?: Submission) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.question = jsonObj.question;
      this.userId = jsonObj.userId;
      this.username = jsonObj.username;
      this.anonymous = jsonObj.anonymous;
    }
  }
}
