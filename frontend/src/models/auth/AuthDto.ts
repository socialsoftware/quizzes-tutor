import User from "@/models/auth/User";

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
