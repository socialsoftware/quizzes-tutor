import Reply from '@/models/management/Reply';
import { ISOtoString } from '@/services/ConvertDateService';
import Question from '@/models/management/Question';

export default class Discussion {
  id!: number;
  question?: Question;
  name!: string;
  username!: string;
  message!: string;
  replies!: Reply[];
  date!: string | null;
  courseExecutionId!: number;
  closed!: boolean;
  lastReplyDate!: string | null;

  constructor(jsonObj?: Discussion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.question = new Question(jsonObj.question);
      this.name = jsonObj.name;
      this.username = jsonObj.username;
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
        this.replies = [];
        this.lastReplyDate = '-';
      }
    }
  }
}
