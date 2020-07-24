import { ISOtoString } from '@/services/ConvertDateService';

export default class Review {
  id!: number;
  userId: number | null = null;
  submissionId: number | null = null;
  comment!: string;
  status!: string;
  creationDate!: string;
  name!: string;

  constructor(jsonObj?: Review) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.submissionId = jsonObj.submissionId;
      this.comment = jsonObj.comment;
      this.status = jsonObj.status;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.name = jsonObj.name;
    }
  }
}
