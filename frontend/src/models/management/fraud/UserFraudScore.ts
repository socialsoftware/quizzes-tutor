import { UserFraudInfo } from './UserFraudInfo';
import { SpeedLevel } from './UserFraudSpeed';

export class UserFraudScore {
  userInfo!: UserFraudInfo;
  scoreTime!: number;
  scoreCommunicationProducer!: number;
  scoreCommunicationConsumer!: number;
  // Answers faster or more evenly than a person can read: a sign, not proof
  speedLevel?: SpeedLevel;
  speedPoints?: number;
  speedReasons?: string[];
  speedAnswered?: number;
  speedTooFast?: number;

  constructor(jsonObj?: UserFraudScore) {
    if (jsonObj) {
      this.userInfo = jsonObj.userInfo;
      this.scoreTime = jsonObj.scoreTime;
      this.scoreCommunicationProducer = jsonObj.scoreCommunicationProducer;
      this.scoreCommunicationConsumer = jsonObj.scoreCommunicationConsumer;
      this.speedLevel = jsonObj.speedLevel;
      this.speedPoints = jsonObj.speedPoints;
      this.speedReasons = jsonObj.speedReasons;
      this.speedAnswered = jsonObj.speedAnswered;
      this.speedTooFast = jsonObj.speedTooFast;
    }
  }
}
