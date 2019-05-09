import axios from "axios";
import Answer from "@/models/Answer";

interface ServerResults {
  answers: Answer[];
}

export default class Results implements ServerResults {
  answers!: Answer[];

  constructor() {}

  static async getAnswers(answers: any, quiz_id: number): Promise<Results> {
    // Make a request for a user with a given ID
    let answersData = [];
    for (let i in answers) {
      answersData.push({
        question_id: parseInt(i),
        option: answers[i],
        date: new Date().toISOString(),
        time_taken: new Date().toISOString()
      });
    }

    let data = {
      student_id: 0,
      answer_date: new Date().toISOString(),
      time_taken: new Date().toISOString(),
      quiz_id: quiz_id,
      answers: answersData
    };

    return axios
      .request<ServerResults>({
        url: "http://localhost:8080/quiz-answers",
        method: "post",
        data: data
      })
      .then(response => {
        const { data } = response;
        return data;
      });
  }
}
