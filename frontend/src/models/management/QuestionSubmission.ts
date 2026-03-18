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
      title: 'Actions',
      key: 'action',
      align: 'start',
      width: '5px',
      sortable: false,
    },
    { title: 'Number', key: 'id', align: 'center', width: '5%' },
    { title: 'Title', key: 'question.title', align: 'center', width: '30%' },
    {
      title: 'Status',
      key: 'status',
      align: 'center',
      width: '150px',
    },
    {
      title: 'Topics',
      key: 'question.topics',
      align: 'center',
      sortable: false,
      width: '35%',
    },
    {
      title: 'Creation Date',
      key: 'question.creationDate',
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