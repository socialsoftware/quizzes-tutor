import { ISOtoString } from '@/services/ConvertDateService';

export default class Review {
  id!: number;
  userId: number | null = null;
  submissionId: number | null = null;
  justification!: string;
  status!: string;
  creationDate!: string;
  username!: string;

  constructor(jsonObj?: Review) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.submissionId = jsonObj.submissionId;
      this.justification = jsonObj.justification;
      this.status = jsonObj.status;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.username = jsonObj.username;
    }
  }
}
