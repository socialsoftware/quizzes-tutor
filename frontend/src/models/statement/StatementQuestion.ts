import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';
import Question from '@/models/management/Question';
import Discussion from '@/models/management/Discussion';

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string;
  image: Image | null = null;
  hasUserDiscussion!: boolean;
  question!: Question;
  discussions: Discussion[] = [];
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.hasUserDiscussion = jsonObj.hasUserDiscussion;
      this.question = jsonObj.question;

      if (jsonObj.discussions) {
        for (let i = 0; i < jsonObj.discussions.length; i++) {
          this.discussions.push(new Discussion(jsonObj.discussions![i]));
        }
      }

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
