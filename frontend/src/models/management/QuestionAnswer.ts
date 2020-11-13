import Question from '@/models/management/Question';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionFactory } from '@/services/QuestionHelpers';

export class QuestionAnswer {
  question!: Question;
  answerDetails!: AnswerDetails;

  constructor(jsonObj?: QuestionAnswer) {
    if (jsonObj) {
      this.question = new Question(jsonObj.question);
      this.answerDetails = QuestionFactory.getFactory(
        jsonObj.answerDetails.type
      ).createAnswerDetails(jsonObj.answerDetails);
    }
  }
}
