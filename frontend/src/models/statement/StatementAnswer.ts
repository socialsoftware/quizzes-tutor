import Discussion from '@/models/management/Discussion';

export default class StatementAnswer {
  public optionId: number | null = null;
  public timeTaken: number = 0;
  public sequence!: number;
  public questionAnswerId!: number;
  public quizQuestionId!: number;
  public userDiscussion?: Discussion;
  public timeToSubmission: number | null = null;

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.userDiscussion = jsonObj.userDiscussion;
    }
  }
}
