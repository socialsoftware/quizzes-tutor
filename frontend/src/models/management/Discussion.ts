import Reply from '@/models/management/Reply';
import { ISOtoString } from '@/services/ConvertDateService';
import Question from '@/models/management/Question';

export default class Discussion {
  id!: number;
  userId!: number;
  question?: Question;
  userName!: string;
  message!: string;
  replies!: Reply[] | null;
  date!: string | null;
  courseExecutionId!: number;
  closed!: boolean;
  lastReplyDate!: string | null;

  constructor(jsonObj?: Discussion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.question = new Question(jsonObj.question);
      this.userName = jsonObj.userName;
      this.message = jsonObj.message;
      this.date = ISOtoString(jsonObj.date);
      this.courseExecutionId = jsonObj.courseExecutionId;
      this.closed = jsonObj.closed;
      if (jsonObj.replies !== null && jsonObj.replies.length > 0) {
        this.replies = jsonObj.replies.map((reply: any) => {
          return new Reply(reply);
        });
        this.lastReplyDate = this.replies[this.replies.length - 1].date;
      } else {
        this.replies = null;
        this.lastReplyDate = '-';
      }
    }
  }
}
