import Image from '@/models/management/Image';
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import { QuestionFactory } from '@/services/QuestionHelpers';

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string;
  image: Image | null = null;

  questionDetails: StatementQuestionDetails = new MultipleChoiceStatementQuestionDetails();

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;

      this.questionDetails = QuestionFactory.getFactory(
        jsonObj.questionDetails.type
      ).createStatementQuestionDetails(jsonObj.questionDetails);
    }
  }
}
