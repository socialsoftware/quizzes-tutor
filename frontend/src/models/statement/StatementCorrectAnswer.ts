import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionFactory } from '@/services/QuestionHelpers';

export default class StatementCorrectAnswer {
  quizQuestionId!: number;
  sequence!: number;

  correctAnswerDetails!: StatementCorrectAnswerDetails;

  constructor(jsonObj?: StatementCorrectAnswer) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.sequence = jsonObj.sequence;

      this.correctAnswerDetails = QuestionFactory.getFactory(
        jsonObj.correctAnswerDetails.type
      ).createStatementCorrectAnswerDetails(jsonObj.correctAnswerDetails);
    }
  }
}
