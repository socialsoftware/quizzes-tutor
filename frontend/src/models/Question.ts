import Option from "@/models/Option";
import Image from "@/models/Image";

export interface ServerQuestion {
  quizQuestionId: number;
  content: string | null;
  options: Option[] | null;
  //topic: string | null;
  image: Image | null;
}

export default class Question implements ServerQuestion {
  quizQuestionId!: number;
  content!: string | null;
  options!: Option[];
  // TODO topic!: string | null;
  image: Image | null;

  constructor(jsonObj: ServerQuestion) {
    this.quizQuestionId = jsonObj.quizQuestionId;
    this.content = jsonObj.content;
    if (jsonObj.options) {
      this.options = jsonObj.options;
    }
    // TODO this.topic = jsonObj.topic;
    this.image = jsonObj.image;
  }
}
