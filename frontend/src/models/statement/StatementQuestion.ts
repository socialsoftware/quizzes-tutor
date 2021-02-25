import Image from '@/models/management/Image';
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import { QuestionFactory } from '@/services/QuestionHelpers';

export default class StatementQuestion {
  quizQuestionId!: number;
  content: string | null = null;
  image: Image | null = null;

  questionDetails: StatementQuestionDetails = new MultipleChoiceStatementQuestionDetails();
  questionId!: number;

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.questionId = jsonObj.questionId;

      if (jsonObj.questionDetails !== null) {
        this.questionDetails = QuestionFactory.getFactory(
          jsonObj.questionDetails.type
        ).createStatementQuestionDetails(jsonObj.questionDetails);
      }
    }
  }
}
