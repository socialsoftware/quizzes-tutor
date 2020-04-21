import Option from '@/models/management/Option';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Question {
  id: number | null = null;
  title: string = '';
  status: string = 'AVAILABLE';
  numberOfAnswers!: number;
  numberOfGeneratedQuizzes!: number;
  numberOfNonGeneratedQuizzes!: number;
  numberOfCorrect!: number;
  difficulty!: number | null;
  content: string = '';
  creationDate!: string | null;
  image: Image | null = null;
  sequence: number | null = null;

  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  topics: Topic[] = [];

  constructor(jsonObj?: Question) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.numberOfGeneratedQuizzes = jsonObj.numberOfGeneratedQuizzes;
      this.numberOfNonGeneratedQuizzes = jsonObj.numberOfNonGeneratedQuizzes;
      this.numberOfCorrect = jsonObj.numberOfCorrect;
      this.difficulty = jsonObj.difficulty;
      this.content = jsonObj.content;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.image = jsonObj.image;

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );

      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
    }
  }
}
