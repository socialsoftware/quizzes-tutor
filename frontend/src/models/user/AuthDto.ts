import User from "@/models/user/User";

export default class AuthDto {
  token!: string;
  user!: User;

  constructor(jsonObj?: AuthDto) {
    if (jsonObj) {
      this.token = jsonObj.token;
      this.user = new User(jsonObj.user);
    }
  }
}
