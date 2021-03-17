import Question from '@/models/management/Question';

export default class QuestionSubmission {
  id!: number;
  courseExecutionId!: number;
  question!: Question;
  submitterId: number | null = null;
  name: string | null = null;
  status: string | null = null;
  studentRead!: boolean;
  teacherRead!: boolean;

  constructor(jsonObj?: QuestionSubmission) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.question = new Question(jsonObj.question);
      this.submitterId = jsonObj.submitterId;
      this.name = jsonObj.name;
      this.status = jsonObj.status;
      this.studentRead = jsonObj.studentRead;
      this.teacherRead = jsonObj.teacherRead;
    }
  }

  static questionSubmissionHeader = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    { text: 'Number', value: 'id', align: 'center', width: '5%' },
    { text: 'Title', value: 'question.title', align: 'center', width: '30%' },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '150px',
    },
    {
      text: 'Topics',
      value: 'question.topics',
      align: 'center',
      sortable: false,
      width: '35%',
    },
    {
      text: 'Creation Date',
      value: 'question.creationDate',
      width: '150px',
      align: 'center',
    },
  ];

  prepareQuestionSubmission(
    courseExecutionId: number,
    submitterId: number,
    question: Question
  ) {
    this.courseExecutionId = courseExecutionId;
    this.submitterId = submitterId;
    this.question = question;
  }

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
    switch (this.status) {
      case 'APPROVED':
        return 'green';
      case 'REJECTED':
        return 'red';
      case 'IN_REVISION':
        return 'yellow';
      case 'IN_REVIEW':
        return 'blue';
    }
  }

  getStatus() {
    return this.status?.replace('_', ' ');
  }
}
