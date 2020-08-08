import User from '@/models/user/User';
export default class AuthDto {
    constructor(jsonObj) {
        if (jsonObj) {
            this.token = jsonObj.token;
            this.user = new User(jsonObj.user);
        }
    }
}
//# sourceMappingURL=AuthDto.js.map