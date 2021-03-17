import QuestionSubmission from '@/models/management/QuestionSubmission';

export default class UserQuestionSubmissionInfo {
  submitterId: number | null = null;
  questionSubmissions: QuestionSubmission[] = [];
  totalQuestionSubmissions: number | null = null;
  numApprovedQuestionSubmissions: number | null = null;
  numRejectedQuestionSubmissions: number | null = null;
  numInReviewQuestionSubmissions: number | null = null;
  numInRevisionQuestionSubmissions: number | null = null;
  username: string | null = null;
  name: string | null = null;

  numQuestionSubmissions: {
    approved: { num: number | null; color: string };
    rejected: { num: number | null; color: string };
    in_review: { num: number | null; color: string };
    in_revision: { num: number | null; color: string };
  } | null = null;

  constructor(jsonObj?: UserQuestionSubmissionInfo) {
    if (jsonObj) {
      this.submitterId = jsonObj.submitterId;
      this.totalQuestionSubmissions = jsonObj.totalQuestionSubmissions;
      this.numApprovedQuestionSubmissions =
        jsonObj.numApprovedQuestionSubmissions;
      this.numRejectedQuestionSubmissions =
        jsonObj.numRejectedQuestionSubmissions;
      this.numInReviewQuestionSubmissions =
        jsonObj.numInReviewQuestionSubmissions;
      this.numInRevisionQuestionSubmissions =
        jsonObj.numInRevisionQuestionSubmissions;
      this.name = jsonObj.name;
      this.username = jsonObj.username;

      this.numQuestionSubmissions = {
        approved: {
          num: jsonObj.numApprovedQuestionSubmissions,
          color: 'green',
        },
        rejected: {
          num: jsonObj.numRejectedQuestionSubmissions,
          color: 'red',
        },
        in_review: {
          num: jsonObj.numInReviewQuestionSubmissions,
          color: 'blue',
        },
        in_revision: {
          num: jsonObj.numInRevisionQuestionSubmissions,
          color: 'yellow',
        },
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
