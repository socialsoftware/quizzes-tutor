export default class ExternalUser {

  name!: string;
  username!: string;
  email!: string;
  password!: string;
  role!: string;
  admin!: boolean;
  confirmationToken!: string;

  constructor(jsonObj?: ExternalUser) {
    if (jsonObj) {
      this.name = jsonObj.name;
			this.username = jsonObj.username;
			this.email = jsonObj.email;
			this.password = jsonObj.password;
      this.role = jsonObj.role;
      this.admin = jsonObj.admin;
      this.confirmationToken = jsonObj.confirmationToken;

    }
  }
}
