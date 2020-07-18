import StatementCorrectAnswerDetails from './questions/StatementCorrectAnswerDetails';
import StatementAnswerDetails from './questions/StatementAnswerDetails';
import { createStatementCorrectAnswerDetails } from '../../services/QuestionHelpers';

export default class StatementCorrectAnswer {
  quizQuestionId!: number;
  sequence!: number;

  correctAnswerDetails!: StatementAnswerDetails;

  constructor(jsonObj?: StatementCorrectAnswer) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.sequence = jsonObj.sequence;

      this.correctAnswerDetails = createStatementCorrectAnswerDetails(
        jsonObj.correctAnswerDetails
      );
    }
  }
}
