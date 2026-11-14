import { UserFraudInfo } from './UserFraudInfo';
import { SpeedLevel } from './UserFraudSpeed';

// What one quiz said about a student's answer times
export interface SpeedQuizResult {
  quizId: number;
  title: string;
  answered: number;
  tooFast: number;
  meanSeconds: number | null;
  points: number;
  level: SpeedLevel;
  // Why, in words
  reasons: string[];
}

// What the quizzes of a course said about a student, together. A pattern that repeats from quiz to quiz
// is a much stronger sign than one quiz; still a sign for the teacher to look at, never proof.
export class StudentSpeedHistory {
  userInfo!: UserFraudInfo;
  quizzesAnalysed: number = 0;
  flaggedQuizzes: number = 0;
  totalPoints: number = 0;
  // Times they tried to open a quiz that opens only once and was already open
  blockedOpenings: number = 0;
  quizzes: SpeedQuizResult[] = [];

  constructor(jsonObj?: StudentSpeedHistory) {
    if (jsonObj) {
      this.userInfo = new UserFraudInfo(jsonObj.userInfo);
      this.quizzesAnalysed = jsonObj.quizzesAnalysed ?? 0;
      this.flaggedQuizzes = jsonObj.flaggedQuizzes ?? 0;
      this.totalPoints = jsonObj.totalPoints ?? 0;
      this.blockedOpenings = jsonObj.blockedOpenings ?? 0;
      this.quizzes = jsonObj.quizzes ?? [];
    }
  }
}

export class ExecutionFraudSpeed {
  quizzesAnalysed: number = 0;
  users: StudentSpeedHistory[] = [];

  constructor(jsonObj?: ExecutionFraudSpeed) {
    if (jsonObj) {
      this.quizzesAnalysed = jsonObj.quizzesAnalysed ?? 0;
      this.users = (jsonObj.users ?? []).map((user) => new StudentSpeedHistory(user));
    }
  }
}
