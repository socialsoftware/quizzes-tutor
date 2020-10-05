import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';
import Question from '@/models/management/Question';
import Discussion from '@/models/management/Discussion';

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string;
  image: Image | null = null;
  question!: Question;
  userDiscussion?: Discussion;
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.question = jsonObj.question;
      this.userDiscussion = jsonObj.userDiscussion;

      if (jsonObj.options) {
        this.options = _.shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        );
      }
    }
  }
}
