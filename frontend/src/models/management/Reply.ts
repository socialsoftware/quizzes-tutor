import { ISOtoString } from '@/services/ConvertDateService';

export default class Reply {
  id!: number;
  userName!: string;
  userId!: number;
  date!: string | null;
  message!: string;
  available!: boolean;

  constructor(jsonObj?: Reply) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userName = jsonObj.userName;
      this.userId = jsonObj.userId;
      this.date = ISOtoString(jsonObj.date);
      this.message = jsonObj.message;
      this.available = jsonObj.available;
    }
  }
}
