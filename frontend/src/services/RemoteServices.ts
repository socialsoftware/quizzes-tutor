import axios from "axios";
import { Question } from "@/models/management/Question";
import { Quiz } from "@/models/management/Quiz";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import Store from "../store";
import StudentStats from "@/models/statement/StudentStats";
import StatementQuiz from "@/models/statement/StatementQuiz";
import SolvedQuiz from "@/models/statement/SolvedQuiz";

interface AuthResponse {
  token: string;
  userRole: string;
}

const httpClient = axios.create();
httpClient.defaults.timeout = 20000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;

export default class RemoteServices {
  static async authenticate(code: string): Promise<AuthResponse> {
    return httpClient
      .post("/auth/fenix", { code: code })
      .then(response => {
        return response.data as AuthResponse;
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get("/stats", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data as StudentStats;
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get("/questions", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static async getActiveQuestions(): Promise<Question[]> {
    return httpClient
      .get("/questions/active", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post("/questions/", question, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data as Question;
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static updateQuestion(questionId: number, question: Question) {
    return httpClient
      .put("/questions/" + questionId, question, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static deleteQuestion(questionId: number) {
    return httpClient
      .delete("/questions/" + questionId, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static questionSwitchActive(questionId: number) {
    return httpClient
      .put("/questions/" + questionId + "/switchActive", null, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append("file", file);
    return httpClient
      .put("/questions/" + questionId + "/image", formData, {
        headers: {
          Authorization: Store.getters.getToken,
          "Content-Type": "multipart/form-data"
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static updateQuestionTopics(questionId: number, topics: string[]) {
    return httpClient.put("/questions/" + questionId + "/topics", topics, {
      headers: {
        Authorization: Store.getters.getToken
      }
    });
  }

  static getTopics(): Promise<string[]> {
    return httpClient
      .get("/topics", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data as string[];
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static getQuizStatement(params: object): Promise<StatementQuiz> {
    return httpClient
      .post("/student/quizzes/generate", params, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get("/student/quizzes/available", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get("/student/quizzes/solved", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static getCorrectAnswers(params: object): Promise<StatementCorrectAnswer[]> {
    return httpClient
      .post("/student/quizzes/answer", params, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data.answers.map((answer: any) => {
          return new StatementCorrectAnswer(answer);
        });
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static createTopic(topic: string) {
    return httpClient
      .post("/topics/", topic, {
        headers: {
          Authorization: Store.getters.getToken,
          "Content-Type": "text/plain"
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static updateTopic(topic: string, newName: string) {
    return httpClient
      .put("/topics/" + topic, newName, {
        headers: {
          Authorization: Store.getters.getToken,
          "Content-Type": "text/plain"
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static deleteTopic(topic: string) {
    return httpClient
      .delete("/topics/" + topic, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get("/quizzes/non-generated", {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static async deleteQuiz(quizId: number) {
    return httpClient
      .delete("/quizzes/" + quizId, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get("/quizzes/" + quizId, {
        headers: {
          Authorization: Store.getters.getToken
        }
      })
      .then(response => {
        return new Quiz(response.data);
      })
      .catch(error => {
        throw Error(this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put("/quizzes/" + quiz.id, quiz, {
          headers: {
            Authorization: Store.getters.getToken
          }
        })
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(error => {
          throw Error(this.errorMessage(error));
        });
    } else {
      return httpClient
        .post("/quizzes/", quiz, {
          headers: {
            Authorization: Store.getters.getToken
          }
        })
        .then(response => {
          return response.data as Quiz;
        })
        .catch(error => {
          throw Error(this.errorMessage(error));
        });
    }
  }

  static errorMessage(error: any): string {
    if (error.code === "ECONNABORTED") {
      return "Timeout: Can not connect to server";
    } else if (error.response) {
      return error.response.data.message;
    } else if (error.request) {
      return "No response received";
    } else {
      return "Error";
    }
  }
}
