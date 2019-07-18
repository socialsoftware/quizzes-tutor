import axios from "axios";
import Question from "./Question";
import CorrectAnswer from "@/models/CorrectAnswer";
import Answer from "@/models/Answer";
import Option from "@/models/Option";

interface ServerQuestion {
  id: number;
  content: string | null;
  options: Option[] | null;
  topic: string | null;
  correctOption: number | null;
  image: {
    url: string | null;
    width: number | null;
  } | null;
}

interface ServerAnswer {
  questionId: number;
  correctOption: number;
}

export default class Quiz {
  private _id: number | null = null;
  private _questions: Question[] = [];
  private _topic: string[] = ["1"];
  private _questionType: string = "new";
  private _numberOfQuestions: string = "5";
  private _correctAnswers: CorrectAnswer[] = [];
  private _answers: Answer[] = [];
  private static quiz: Quiz = new Quiz();

  static get getInstance(): Quiz {
    return this.quiz;
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
  get questions(): Question[] {
    return this._questions;
  }

  set questions(value: Question[]) {
    this._questions = value;
  }
  get id(): number | null {
    return this._id;
  }

  set id(value: number | null) {
    this._id = value;
  }

  get answers(): Answer[] {
    return this._answers;
  }

  set answers(value: Answer[]) {
    this._answers = value;
  }
  get correctAnswers(): CorrectAnswer[] {
    return this._correctAnswers;
  }

  set correctAnswers(value: CorrectAnswer[]) {
    this._correctAnswers = value;
  }

  get correctAnswersNumber(): number | null {
    if (this.answers.length !== 0 && this.correctAnswers.length !== 0) {
      return this.answers
        .map((answer, index) => {
          return +(this.correctAnswers[index].correctOption === answer.option);
        })
        .reduce((acc, curr) => {
          return acc + curr;
        });
    }
    return null;
  }

  async getQuestions() {
    let params = {
      topic: this.topic,
      questionType: this.questionType,
      numberOfQuestions: +this.numberOfQuestions
    };

    return await axios
      .post(process.env.VUE_APP_ROOT_API + "/newquiz", params)
      .then(response => {
        // handle success
        this.id = response.data["id"];
        (response.data["questions"] as Array<ServerQuestion>).forEach(
          question => {
            this.questions.push(new Question(question));
            this.answers.push(new Answer(question.id, null, new Date()));
          }
        );
      });
  }

  async getCorrectAnswers() {
    let params = {
      answerDate: new Date().toISOString(),
      quizId: this.id,
      answers: this.answers
    };

    return axios
      .post(process.env.VUE_APP_ROOT_API + "/quiz-answers", params)
      .then(response => {
        (response.data["answers"] as Array<ServerAnswer>).forEach(answer => {
          this.correctAnswers.push(new CorrectAnswer(answer));
        });
      });
  }

  reset() {
    this.id = null;
    this.questions = [];
    this.answers = [];
    this.correctAnswers = [];
  }

  isEmpty(): boolean {
    return this.id === undefined || this.questions.length === 0;
  }
}
