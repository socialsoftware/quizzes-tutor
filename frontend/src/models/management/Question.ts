import Option from "@/models/management/Option";
import Image from "@/models/management/Image";
import { Topic } from "@/models/management/Topic";

export class Question {
  id!: number;
  title!: string;
  active!: boolean;
  numberOfAnswers!: number;
  difficulty!: number;
  content!: string;
  options!: Option[];
  image!: Image | null;
  topics: Topic[] = [];
  sequence: number | null = null;

  constructor(jsonObj?: Question) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.active = jsonObj.active;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.difficulty = jsonObj.difficulty;
      this.content = jsonObj.content;
      this.options = jsonObj.options;
      this.image = jsonObj.image;
      this.topics = jsonObj.topics;
    }
  }
}
