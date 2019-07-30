import axios from "axios";
import Question, { QuestionDto } from "@/models/student/Question";
import CorrectAnswer, {
  CorrectAnswerDto
} from "@/models/student/CorrectAnswer";
import Answer from "@/models/student/Answer";

export default class Quiz {
  id: number | null = null;
  questions: Question[] = [];
  answers: Answer[] = [];
  correctAnswers: CorrectAnswer[] = [];

  topic: string[] = ["1"];
  questionType: string = "new";
  numberOfQuestions: string = "5";

  private static _quiz: Quiz = new Quiz();

  static get getInstance(): Quiz {
    return this._quiz;
  }

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
        this.questions = response.data["questions"].map(
          (question: QuestionDto) => new Question(question)
        );

        this.answers = response.data["questions"].map(
          (question: QuestionDto) => new Answer(question.quizQuestionId)
        );
      });
  }

  async getCorrectAnswers() {
    let params = {
      answerDate: new Date().toISOString(),
      quizAnswerId: this.id,
      answers: this.answers
    };

    return axios
      .post(process.env.VUE_APP_ROOT_API + "/quiz-answers", params)
      .then(response => {
        this.correctAnswers = response.data["answers"].map(
          (correctAnswerDto: CorrectAnswerDto) =>
            new CorrectAnswer(correctAnswerDto)
        );
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
