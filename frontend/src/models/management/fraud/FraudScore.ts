import { UserFraudInfo } from "./UserFraudInfo";

export class FraudScore {
  userInfo!: UserFraudInfo;
  score!: number;

  constructor(jsonObj?: FraudScore) {
    if (jsonObj) {
      this.userInfo = new UserFraudInfo(jsonObj.userInfo);
      if (jsonObj.score) this.score = jsonObj.score;
    }
  }
}
