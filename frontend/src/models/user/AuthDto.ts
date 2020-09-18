import AuthUser from '@/models/user/AuthUser';

export default class AuthDto {
  token!: string;
  user!: AuthUser;

  constructor(jsonObj?: AuthDto) {
    if (jsonObj) {
      this.token = jsonObj.token;
      this.user = new AuthUser(jsonObj.user);
    }
  }
}
