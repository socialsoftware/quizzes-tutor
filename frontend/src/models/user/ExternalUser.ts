export default class ExternalUser {
  id!: number;
  name!: string;
  username!: string;
  email!: string;
  password!: string;
  role!: string;
  state!: string;
  admin!: boolean;
  confirmationToken!: string;

  constructor(jsonObj?: ExternalUser) {
    if (jsonObj) {
      this.id = jsonObj.id;
			this.username = jsonObj.username;
			this.email = jsonObj.email;
			this.password = jsonObj.password;
      this.role = jsonObj.role;
      this.state = jsonObj.state;
      this.admin = jsonObj.admin;
      this.confirmationToken = jsonObj.confirmationToken;

    }
  }
}
