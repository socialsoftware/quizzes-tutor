export class UserFraudInfo {
    username!: string;
    name!: string;
    user_id!: string;
  
    constructor(jsonObj?: UserFraudInfo) {
      if (jsonObj) {
        this.username = jsonObj.username;
        this.name = jsonObj.name;
        this.user_id = jsonObj.user_id;
      }
    }
  }
  