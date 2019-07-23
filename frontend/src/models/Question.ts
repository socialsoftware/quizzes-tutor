import Option from "@/models/Option";
import { ServerQuestion, Image } from "@/types";

export default class Question implements ServerQuestion {
  id!: number;
  content!: string | null;
  options!: Option[] | null;
  // TODO topic!: string | null;
  image: Image | null;

  constructor(jsonObj: ServerQuestion) {
    this.id = jsonObj.id;
    this.content = jsonObj.content;
    this.options = jsonObj.options;
    // TODO this.topic = jsonObj.topic;
    this.image = jsonObj.image;
  }
}
