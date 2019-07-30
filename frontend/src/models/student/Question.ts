import Option from "@/models/student/Option";
import Image from "@/models/student/Image";
import { _ } from "vue-underscore";

export interface QuestionDto {
  quizQuestionId: number;
  content: string | null;
  options: Option[] | null;
  //topic: string | null;
  image: Image | null;
}

export default class Question implements QuestionDto {
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
