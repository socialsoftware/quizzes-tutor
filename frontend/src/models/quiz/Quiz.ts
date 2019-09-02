import { QuestionDto } from "@/models/question/Question";

export interface QuizDto {
    id: number;
    number: number;
    title: string;
    date: Date;
    type: string;
    year: number;
    series: number;
    version: string;
    numberOfQuestions: number;
    numberOfAnswers: number;
    questions: QuestionDto[] | null ;
  }
  
  export class Quiz implements QuizDto {
    id: number;
    number: number;
    title: string;
    date: Date;
    type: string;
    year: number;
    series: number;
    version: string;
    numberOfQuestions: number;
    numberOfAnswers: number;
    questions: QuestionDto[] | null;

    constructor(jsonObj: QuizDto) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.title = jsonObj.title;
      this.date = jsonObj.date;
      this.type = jsonObj.type;
      this.year = jsonObj.year;
      this.series = jsonObj.series;
      this.version = jsonObj.version;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.numberOfAnswers = jsonObj.numberOfAnswers;
      this.questions = jsonObj.questions;
    }
  }