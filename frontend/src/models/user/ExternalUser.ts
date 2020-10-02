import User from '@/models/user/User';

export default class ExternalUser extends User {
  password!: string;
  state!: string;
  confirmationToken!: string;

  constructor(jsonObj?: ExternalUser) {
    super(jsonObj);
    if (jsonObj) {
      this.password = jsonObj.password;
      this.state = jsonObj.state;
      this.confirmationToken = jsonObj.confirmationToken;
    }
  }
}
