import axios from "axios";
import Question from "./Question";

export default class Quiz {
  questions: Question[] = [];

  constructor() {}

  async getQuestions(): Promise<Question[]> {
    // Make a request for a user with a given ID
    return await axios.get("http://localhost:8080/newquiz").then(response => {
      // handle success
      for (let i = 0; i < response.data["questions"].length; i++) {
        let a1 = new Question(response.data["questions"][i]);
        this.questions.push(a1);
      }
      return this.questions;
    });
  }

  getJson(): Promise<string[]> {
    if (this.questions.length == 0) {
      return this.getQuestions().then(questions => {
        return this.createJson(questions);
      });
    } else {
      return new Promise(resolve => resolve(this.createJson(this.questions)));
    }
  }

  createJson(questions: Question[]): string[] {
    let a = [];
    for (let question of questions) {
      a.push(question.customjson());
    }
    // a.push(questions[0].customjson());
    // a.push(questions[1].customjson());
    return a;
  }
}
