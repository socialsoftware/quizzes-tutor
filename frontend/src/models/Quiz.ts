import axios from "axios";
import Question from "./Question";
import Store from "@/store";

export default class Quiz {
  static id: number;
  static questions: Question[] = [];

  constructor() {}

  static async getQuiz(
    topic: string,
    questions: string,
    numberOfQuestions: number
  ): Promise<Question[]> {
    let params = {
      params: {
        token: Store.getters.getToken,
        topic: topic,
        questions: questions,
        numberOfQuestions: numberOfQuestions
      }
    };

    return await axios
      .get(process.env.VUE_APP_ROOT_API + "/newquiz", params)
      .then(response => {
        // handle success
        this.id = response.data["id"];
        for (let i = 0; i < response.data["questions"].length; i++) {
          let a1 = new Question(response.data["questions"][i]);
          this.questions.push(a1);
        }
        return this.questions;
      });
  }

  static getQuestions() {
    return this.questions;
  }

  static getId() {
    return this.id;
  }
}
