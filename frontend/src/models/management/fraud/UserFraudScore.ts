import { UserFraudInfo } from "@/models/management/fraud/UserFraudInfo";

export class UserFraudScore {
  userInfo!: UserFraudInfo;
  scoreTime!: number;
  scoreCommunicationProducer!: number;
  scoreCommunicationConsumer!: number;

  constructor(jsonObj?: UserFraudScore) {
    if (jsonObj) {
      this.userInfo = jsonObj.userInfo;
      this.scoreTime = jsonObj.scoreTime;
      this.scoreCommunicationProducer = jsonObj.scoreCommunicationProducer;
      this.scoreCommunicationConsumer = jsonObj.scoreCommunicationConsumer;
    }
  }
}
