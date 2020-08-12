import Question from '@/models/management/Question';

export default class QuestionSubmission {
  id!: number;
  courseExecutionId!: number;
  question!: Question;
  userId: number | null = null;
  name: string | null = null;

  constructor(jsonObj?: QuestionSubmission) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.question = new Question(jsonObj.question);
      this.userId = jsonObj.userId;
      this.name = jsonObj.name;
    }
  }

  isInDiscussion() {
    return (
      this.question.status === 'IN_REVISION' ||
      this.question.status === 'IN_REVIEW'
    );
  }

  isInRevision() {
    return this.question.status === 'IN_REVISION';
  }

  isRejected() {
    return this.question.status === 'REJECTED';
  }

  getStatusColor() {
    if (this.question.status === 'AVAILABLE') return 'green';
    else if (this.question.status === 'DISABLED') return 'orange';
    else if (this.question.status === 'REJECTED') return 'red';
    else if (this.question.status === 'IN_REVISION') return 'yellow';
    else if (this.question.status === 'IN_REVIEW') return 'blue';
  }
}
