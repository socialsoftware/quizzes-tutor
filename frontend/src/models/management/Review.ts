import { ISOtoString } from '@/services/ConvertDateService';

export default class Review {
  id!: number;
  userId: number | null = null;
  questionSubmissionId: number | null = null;
  comment!: string;
  submissionStatus!: string;
  creationDate!: string;
  name!: string;
  username!: string;

  static statusOptions = [
    { text: 'Comment', value: null },
    { text: 'Request Changes', value: 'IN_REVISION', color: 'yellow' },
    { text: 'Request Further Review', value: 'IN_REVIEW', color: 'blue' },
    { text: 'Approve', value: 'APPROVED', color: 'green' },
    { text: 'Reject', value: 'REJECTED', color: 'red' }
  ];

  constructor(jsonObj?: Review) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.questionSubmissionId = jsonObj.questionSubmissionId;
      this.comment = jsonObj.comment;
      this.submissionStatus = jsonObj.submissionStatus;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.name = jsonObj.name;
      this.username = jsonObj.username;
    }
  }

  prepareReview(
    questionSubmissionId: number,
    status: string,
    comment: string,
    userId: number
  ) {
    this.questionSubmissionId = questionSubmissionId;
    this.submissionStatus = status;
    this.userId = userId;
    this.comment = status !== null ? status + '@@' + comment : comment; // explain @@
  }

  getSubmissionStatus() {
    let comment = this.comment.split('@@');
    return comment.length > 1 ? comment[0].replace('_', ' ') : null;
  }

  getComment() {
    let comment = this.comment.split('@@');
    return comment.length > 1 ? comment[1] : this.comment;
  }

  getStatusColor() {
    let comment = this.comment.split('@@');

    switch (comment[0]) {
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

  isComment() {
    return this.submissionStatus === null;
  }

  isInRevision() {
    return this.getSubmissionStatus() === 'IN REVISION';
  }

  isUsersReview(username: string) {
    return username === this.username;
  }
}
