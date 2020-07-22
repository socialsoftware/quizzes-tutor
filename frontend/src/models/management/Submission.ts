import Question from '@/models/management/Question';

export default class Submission {
  id!: number;
  courseExecutionId!: number;
  question!: Question;
  userId: number | null = null;
  name: string | null = null;

  constructor(jsonObj?: Submission) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.question = new Question(jsonObj.question);
      this.userId = jsonObj.userId;
      this.name = jsonObj.name;
    }
  }
}
