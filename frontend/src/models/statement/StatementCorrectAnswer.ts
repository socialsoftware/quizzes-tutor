import StatementCorrectAnswerDetails from './questions/StatementCorrectAnswerDetails';
import { createStatementCorrectAnswerDetails } from '../../services/QuestionHelpers';

export default class StatementCorrectAnswer {
  quizQuestionId!: number;
  sequence!: number;

  correctAnswerDetails!: StatementCorrectAnswerDetails;

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
