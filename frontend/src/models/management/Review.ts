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
    { text: 'Request Changes', value: 'IN_REVISION' },
    { text: 'Request Further Review', value: 'IN_REVIEW' },
    { text: 'Approve', value: 'APPROVED' },
    { text: 'Reject', value: 'REJECTED' }
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

  isComment() {
    return this.submissionStatus === null;
  }

  isUsersReview(username: string) {
    return username === this.username;
  }
}
