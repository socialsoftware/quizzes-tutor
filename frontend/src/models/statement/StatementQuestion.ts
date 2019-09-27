import StatementOption from "@/models/statement/StatementOption";
import Image from "@/models/management/Image";
import { _ } from "vue-underscore";

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string | null;
  options!: StatementOption[];
  image: Image | null = null;

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      if (jsonObj.options) {
        this.options = _.shuffle(jsonObj.options);
      }
      this.image = jsonObj.image;
    }
  }
}
