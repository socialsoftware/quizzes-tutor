import axios from "axios";
import { Question } from "@/models/management/Question";
import StatementQuestion from "@/models/statement/StatementQuestion";
import { Quiz } from "@/models/management/Quiz";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import Store from "../store";
import StudentStats from "@/models/statement/StudentStats";

interface QuizAnswer {
  quizAnswerId: number;
  questions: StatementQuestion[];
}

axios.defaults.headers.common["Authorization"] = Store.getters.getToken;
const httpClient = axios.create();
httpClient.defaults.timeout = 5000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;

export default class RemoteServices {
  static authenticate(code: string) {
    return new Promise((resolve, reject) => {
      httpClient
        .post("/auth/fenix", { code: code })
        .then(response => {
          return response.data;
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get("/stats")
      .then(response => {
        return response.data as StudentStats;
      })
      .catch(error => {
        throw error;
      });
  }

  static getQuestions(): Promise<Question[]> {
    return httpClient
      .get("/questions")
      .then(response => {
        return response.data as Question[];
      })
      .catch(error => {
        return error;
      });
  }

  static createQuestion(question: Question): Promise<Question> {
    return httpClient.post("/questions/", question);
  }

  static updateQuestion(questionId: number, question: Question) {
    return httpClient.put("/questions/" + questionId, question);
  }

  static deleteQuestion(questionId: number) {
    return httpClient.delete("/questions/" + questionId);
  }

  static questionSwitchActive(questionId: number) {
    return httpClient.put("/questions/" + questionId + "/switchActive");
  }

  static uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append("file", file);
    return httpClient.put("/questions/" + questionId + "/image", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    });
  }

  static updateQuestionTopics(questionId: number, topics: string[]) {
    return httpClient.put("/questions/" + questionId + "/topics", topics);
  }

  static getTopics(): Promise<string[]> {
    return httpClient
      .get("/topics")
      .then(response => {
        return response.data as string[];
      })
      .catch(error => {
        if (error.code === "ECONNABORTED") {
          throw Error("Timeout: Can not connect to server");
        } else {
          throw new Error("Error");
        }
      });
  }

  static getQuizAnswer(params: object): Promise<QuizAnswer> {
    return httpClient
      .post("/quizzes/generate/student", params)
      .then(response => {
        return response.data as QuizAnswer;
      });
  }

  static getCorrectAnswers(params: object): Promise<StatementCorrectAnswer[]> {
    return httpClient
      .post("/quiz-answers", params)
      .then(response => {
        return response.data as StatementCorrectAnswer[];
      })
      .catch(error => {
        if (error.code === "ECONNABORTED") {
          throw Error("Timeout: Can not connect to server");
        } else {
          throw new Error("Error");
        }
      });
  }

  static createTopic(topic: string) {
    return httpClient.post("/topics/", topic, {
      headers: { "Content-Type": "text/plain" }
    });
  }

  static updateTopic(topic: string, newName: string) {
    return httpClient.put("/topics/" + topic, newName, {
      headers: { "Content-Type": "text/plain" }
    });
  }

  static deleteTopic(topic: string) {
    return httpClient.delete("/topics/" + topic);
  }

  static getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get("/quizzes/nongenerated")
      .then(response => {
        return response.data as Quiz[];
      })
      .catch(error => {
        if (error.response) {
          throw new Error(error.response.data.message);
        } else if (error.request) {
          throw new Error("No response received");
        } else {
          throw new Error("Error");
        }
      });
  }
}
