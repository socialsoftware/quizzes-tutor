import QuestionSubmission from '@/models/management/QuestionSubmission';

export default class UserQuestionSubmissionInfo {
  userId: number | null = null;
  questionSubmissions: QuestionSubmission[] = [];
  totalQuestionSubmissions: number | null = null;
  numAvailableQuestionSubmissions: number | null = null;
  numDisabledQuestionSubmissions: number | null = null;
  numRejectedQuestionSubmissions: number | null = null;
  numInReviewQuestionSubmissions: number | null = null;
  numInRevisionQuestionSubmissions: number | null = null;
  username: string | null = null;
  name: string | null = null;

  numQuestionSubmissions: {
    available: { num: number | null; color: string };
    disabled: { num: number | null; color: string };
    rejected: { num: number | null; color: string };
    in_review: { num: number | null; color: string };
    in_revision: { num: number | null; color: string };
  } | null = null;

  constructor(jsonObj?: UserQuestionSubmissionInfo) {
    if (jsonObj) {
      this.userId = jsonObj.userId;
      this.totalQuestionSubmissions = jsonObj.totalQuestionSubmissions;
      this.numAvailableQuestionSubmissions =
        jsonObj.numAvailableQuestionSubmissions;
      this.numDisabledQuestionSubmissions =
        jsonObj.numDisabledQuestionSubmissions;
      this.numRejectedQuestionSubmissions =
        jsonObj.numRejectedQuestionSubmissions;
      this.numInReviewQuestionSubmissions =
        jsonObj.numInReviewQuestionSubmissions;
      this.numInRevisionQuestionSubmissions =
        jsonObj.numInRevisionQuestionSubmissions;
      this.name = jsonObj.name;
      this.username = jsonObj.username;

      this.numQuestionSubmissions = {
        available: {
          num: jsonObj.numAvailableQuestionSubmissions,
          color: 'green'
        },
        disabled: {
          num: jsonObj.numDisabledQuestionSubmissions,
          color: 'orange'
        },
        rejected: {
          num: jsonObj.numRejectedQuestionSubmissions,
          color: 'red'
        },
        in_review: {
          num: jsonObj.numInReviewQuestionSubmissions,
          color: 'blue'
        },
        in_revision: {
          num: jsonObj.numInRevisionQuestionSubmissions,
          color: 'yellow'
        }
      };

      this.questionSubmissions = jsonObj.questionSubmissions.map(
        (questionSubmission: QuestionSubmission) =>
          new QuestionSubmission(questionSubmission)
      );
    }
  }

  hasNoSubmissions() {
    return this.questionSubmissions.length === 0;
  }
}
