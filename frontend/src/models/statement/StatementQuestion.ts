import Option from "@/models/management/Option";
import Image from "@/models/management/Image";
import { _ } from "vue-underscore";

/*export interface QuestionDto {
  quizQuestionId: number;
  content: string | null;
  options: Option[] | null;
  //topic: string | null;
  image: Image | null;
}

export class Question implements QuestionDto {
  quizQuestionId!: number;
  content!: string | null;
  options!: Option[];
  // TODO topic!: string | null;
  image: Image | null;

  constructor(jsonObj: QuestionDto) {
    this.quizQuestionId = jsonObj.quizQuestionId;
    this.content = jsonObj.content;
    if (jsonObj.options) {
      this.options = _.shuffle(jsonObj.options);
    }
    // TODO this.topic = jsonObj.topic;
    this.image = jsonObj.image;
  }
}
*/

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string | null;
  options!: Option[];
  // TODO topic!: string | null;
  image: Image | null = null;
}
