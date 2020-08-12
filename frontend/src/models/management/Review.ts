import { ISOtoString } from '@/services/ConvertDateService';

export default class Review {
  id!: number;
  userId: number | null = null;
  questionSubmissionId: number | null = null;
  comment!: string;
  status!: string;
  creationDate!: string;
  name!: string;
  username!: string;

  static statusOptions = [
    { text: 'Comment', value: 'COMMENT' },
    { text: 'Request Changes', value: 'IN_REVISION' },
    { text: 'Request Further Review', value: 'IN_REVIEW' },
    { text: 'Available', value: 'AVAILABLE' },
    { text: 'Disabled', value: 'DISABLED' },
    { text: 'Rejected', value: 'REJECTED' }
  ];

  constructor(jsonObj?: Review) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.questionSubmissionId = jsonObj.questionSubmissionId;
      this.comment = jsonObj.comment;
      this.status = jsonObj.status;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.name = jsonObj.name;
      this.username = jsonObj.username;
    }
  }

  isComment() {
    return this.status === 'COMMENT';
  }

  isUsersReview(username: string) {
    return username === this.username;
  }

  reviewStatus() {
    if (this.status === 'AVAILABLE' || this.status === 'DISABLED') {
      return 'APPROVED';
    } else if (this.status === 'IN_REVISION') {
      return 'QUESTION STATUS';
    } else if (this.status === 'IN_REVIEW') {
      return 'FURTHER REVIEW REQUESTED';
    } else {
      return this.status;
    }
  }

  getStatusColor() {
    if (this.status === 'AVAILABLE' || this.status === 'DISABLED')
      return 'green';
    else if (this.status === 'REJECTED') return 'red';
    else if (this.status === 'IN_REVISION') return 'yellow';
    else if (this.status === 'IN_REVIEW') return 'blue';
  }
}
