import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import MultipleChoiceStatementAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementAnswerDetails';
import { QuestionFactory } from '@/services/QuestionHelpers';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Discussion from '@/models/management/Discussion';

export default class StatementAnswer {
  public timeTaken: number = 0;
  public timeToSubmission: number | null = null;
  public sequence!: number;
  public quizAnswerId!: number;
  public questionAnswerId!: number;
  public questionId!: number;
  public quizQuestionId!: number;
  public userDiscussion?: Discussion;

  answerDetails: StatementAnswerDetails =
    new MultipleChoiceStatementAnswerDetails();

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.questionId = jsonObj.questionId;
      this.quizQuestionId = jsonObj.quizQuestionId;

      this.answerDetails = QuestionFactory.getFactory(
        jsonObj.answerDetails.type
      ).createStatementAnswerDetails(jsonObj.answerDetails);
      this.userDiscussion = jsonObj.userDiscussion;
    }
  }

  isQuestionAnswered(): boolean {
    return this.answerDetails.isQuestionAnswered();
  }

  isAnswerCorrect(correctAnswer: StatementCorrectAnswer): boolean {
    return this.answerDetails.isAnswerCorrect(
      correctAnswer.correctAnswerDetails
    );
  }
}
