import axios from "axios";
import Question from "@/models/Question";
import CorrectAnswer from "@/models/CorrectAnswer";
import Answer from "@/models/Answer";
import { map_to_object } from "@/scripts/script";
import Option from "@/models/Option";

export interface ServerQuestion {
  quizQuestionId: number;
  content: string | null;
  options: Option[] | null;
  //topic: string | null;
  image: Image | null;
}

export interface Image {
  url: string;
  width: number | null;
}

export default class Quiz {
  private _id: number | null = null;

  private _questions: Map<number, Question> = new Map<number, Question>();
  private _answers: Map<number, Answer> = new Map<number, Answer>();
  private _correctAnswers: Map<number, CorrectAnswer> = new Map<
    number,
    CorrectAnswer
  >();

  private _topic: string[] = ["1"];
  private _questionType: string = "new";
  private _numberOfQuestions: string = "5";

  private static _quiz: Quiz = new Quiz();

  static get getInstance(): Quiz {
    return this._quiz;
  }

  /*get correctAnswersNumber(): number | null {
    if (this.answers.size !== 0 && this.correctAnswers.size !== 0) {
      return this.answers
        .map((answer, index) => {
          return +(this.correctAnswers[index].correctOption === answer.option);
        })
        .reduce((acc, curr) => {
          return acc + curr;
        });
    }
    return null;
  }*/

  async getQuestions() {
    let params = {
      topic: this.topic,
      questionType: this.questionType,
      numberOfQuestions: +this.numberOfQuestions
    };

    return await axios
      .post(process.env.VUE_APP_ROOT_API + "/quizzes/generate/student", params)
      .then(response => {
        // handle success
        this.id = response.data["quizAnswerId"];

        Object.keys(response.data["questions"]).forEach(sequence => {
          this.questions.set(
            +sequence,
            new Question(response.data["questions"][sequence])
          );
          this.answers.set(
            +sequence,
            new Answer(response.data["questions"][sequence].id)
          );
        });
      });
  }

  async getCorrectAnswers() {
    let params = {
      answerDate: new Date().toISOString(),
      quizId: this.id,
      answers: map_to_object(this.answers)
    };

    return axios
      .post(process.env.VUE_APP_ROOT_API + "/quiz-answers", params)
      .then(response => {
        Object.keys(response.data["answers"]).forEach(sequence => {
          this.correctAnswers.set(
            +sequence,
            new CorrectAnswer(response.data["answers"][sequence])
          );
        });
      });
  }

  reset() {
    this.id = null;
    this.questions = new Map<number, Question>();
    this.answers = new Map<number, Answer>();
    this.correctAnswers = new Map<number, CorrectAnswer>();
  }

  isEmpty(): boolean {
    return this.id === undefined || this.questions.size === 0;
  }

  get id(): number | null {
    return this._id;
  }

  set id(value: number | null) {
    this._id = value;
  }

  get questions(): Map<number, ServerQuestion> {
    return this._questions;
  }

  set questions(value: Map<number, ServerQuestion>) {
    this._questions = value;
  }

  get numberOfQuestions(): string {
    return this._numberOfQuestions;
  }

  set numberOfQuestions(value: string) {
    this._numberOfQuestions = value;
  }
  get questionType(): string {
    return this._questionType;
  }

  set questionType(value: string) {
    this._questionType = value;
  }
  get topic(): string[] {
    return this._topic;
  }

  set topic(value: string[]) {
    this._topic = value;
  }

  get answers(): Map<number, Answer> {
    return this._answers;
  }

  set answers(value: Map<number, Answer>) {
    this._answers = value;
  }

  get correctAnswers(): Map<number, CorrectAnswer> {
    return this._correctAnswers;
  }

  set correctAnswers(value: Map<number, CorrectAnswer>) {
    this._correctAnswers = value;
  }
}
