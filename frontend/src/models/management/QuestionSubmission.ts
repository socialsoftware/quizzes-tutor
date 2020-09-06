import Question from '@/models/management/Question';

export default class QuestionSubmission {
  id!: number;
  courseExecutionId!: number;
  question!: Question;
  submitterId: number | null = null;
  name: string | null = null;
  status: string | null = null;

  constructor(jsonObj?: QuestionSubmission) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.question = new Question(jsonObj.question);
      this.submitterId = jsonObj.submitterId;
      this.name = jsonObj.name;
      this.status = jsonObj.status;
    }
  }

  static questionSubmissionHeader = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false
    },
    { text: 'Title', value: 'question.title', align: 'center', width: '20%' },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '150px'
    },
    {
      text: 'Topics',
      value: 'question.topics',
      align: 'center',
      sortable: false,
      width: '50%'
    },
    {
      text: 'Creation Date',
      value: 'question.creationDate',
      width: '150px',
      align: 'center'
    }
  ];

  isInDiscussion() {
    return this.status === 'IN_REVISION' || this.status === 'IN_REVIEW';
  }

  isInRevision() {
    return this.status === 'IN_REVISION';
  }

  isRejected() {
    return this.status === 'REJECTED';
  }

  getStatusColor() {
    if (this.status === 'APPROVED') return 'green';
    else if (this.status === 'REJECTED') return 'red';
    else if (this.status === 'IN_REVISION') return 'yellow';
    else if (this.status === 'IN_REVIEW') return 'blue';
  }
}
