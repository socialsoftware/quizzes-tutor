import { UserFraudInfo } from './UserFraudInfo';

export type SpeedLevel = 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH';

// A student whose answers came faster, or more evenly, than a person can read and decide.
// A sign for the teacher to look at, never proof: the reasons say why in words.
export class UserFraudSpeed {
  userInfo!: UserFraudInfo;
  answered: number = 0;
  tooFast: number = 0;
  meanSeconds: number | null = null;
  points: number = 0;
  level: SpeedLevel = 'NONE';
  reasons: string[] = [];

  constructor(jsonObj?: UserFraudSpeed) {
    if (jsonObj) {
      this.userInfo = new UserFraudInfo(jsonObj.userInfo);
      this.answered = jsonObj.answered ?? 0;
      this.tooFast = jsonObj.tooFast ?? 0;
      this.meanSeconds = jsonObj.meanSeconds ?? null;
      this.points = jsonObj.points ?? 0;
      this.level = jsonObj.level ?? 'NONE';
      this.reasons = jsonObj.reasons ?? [];
    }
  }
}
