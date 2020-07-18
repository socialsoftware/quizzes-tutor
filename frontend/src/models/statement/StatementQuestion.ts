import Image from '@/models/management/Image';
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import { createStatementQuestionDetails } from '@/models/management/questions/Helpers';

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

      this.questionDetails = createStatementQuestionDetails(
        jsonObj.questionDetails
      );
    }
  }
}
