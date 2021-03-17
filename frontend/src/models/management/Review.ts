import { ISOtoString } from '@/services/ConvertDateService';

export default class Review {
  id!: number;
  userId: number | null = null;
  questionSubmissionId: number | null = null;
  comment!: string;
  type!: string;
  creationDate!: string;
  name!: string;
  username!: string;

  static statusOptions = [
    { text: 'Comment', value: 'COMMENT' },
    { text: 'Request Changes', value: 'REQUEST_CHANGES', color: 'yellow' },
    { text: 'Approve', value: 'APPROVE', color: 'green' },
    { text: 'Reject', value: 'REJECT', color: 'red' },
  ];

  constructor(jsonObj?: Review) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.questionSubmissionId = jsonObj.questionSubmissionId;
      this.comment = jsonObj.comment;
      this.type = jsonObj.type;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.name = jsonObj.name;
      this.username = jsonObj.username;
    }
  }

  prepareReview(
    questionSubmissionId: number,
    type: string,
    comment: string,
    userId: number
  ) {
    this.questionSubmissionId = questionSubmissionId;
    this.type = type;
    this.userId = userId;
    this.comment = comment;
  }

  getStatusColor() {
    switch (this.type) {
      case 'APPROVE':
        return 'green';
      case 'REJECT':
        return 'red';
      case 'REQUEST_CHANGES':
        return 'yellow';
      case 'REQUEST_REVIEW':
        return 'blue';
    }
  }

  getType(firstReview: boolean) {
    switch (this.type) {
      case 'APPROVE':
        return 'APPROVED';
      case 'REJECT':
        return 'REJECTED';
      case 'REQUEST_CHANGES':
        return 'CHANGES REQUESTED';
      case 'REQUEST_REVIEW':
        return firstReview ? 'NEW SUBMISSION' : 'SUBMISSION EDITED';
    }
  }

  isComment() {
    return this.type === 'COMMENT';
  }

  isUsersReview(username: string) {
    return username === this.username;
  }
}
