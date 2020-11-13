import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionFactory } from '@/services/QuestionHelpers';
import { ISOtoString } from '@/services/ConvertDateService';
import MultipleChoiceQuestionDetails from './questions/MultipleChoiceQuestionDetails';

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

  questionDetailsDto: QuestionDetails = new MultipleChoiceQuestionDetails();

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

      this.questionDetailsDto = QuestionFactory.getFactory(
        jsonObj.questionDetailsDto.type
      ).createQuestionDetails(jsonObj.questionDetailsDto);

      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
    }
  }
}
