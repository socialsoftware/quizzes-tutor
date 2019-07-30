import Option from "@/models/student/Option";
import Image from "@/models/student/Image";

export interface QuestionDto {
  id: number;
  title: string;
  active: boolean;
  numberOfAnswers: number;
  difficulty: number;
  content: string | null;
  options: Option[];
  image: Image | null;
}

export class Question implements QuestionDto {
  id!: number;
  title: string;
  active: boolean;
  numberOfAnswers: number;
  difficulty: number;
  content!: string | null;
  options!: Option[];
  image: Image | null;

  constructor(jsonObj: QuestionDto) {
    this.id = jsonObj.id;
    this.title = jsonObj.title;
    this.active = jsonObj.active;
    this.numberOfAnswers = jsonObj.numberOfAnswers;
    this.difficulty = jsonObj.difficulty;
    this.content = jsonObj.content;
    this.options = jsonObj.options;
    this.image = jsonObj.image;
  }
}
