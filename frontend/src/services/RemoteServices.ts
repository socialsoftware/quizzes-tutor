import axios from "axios";
import { Question } from "@/models/question/Question"
import { Quiz } from "@/models/quiz/Quiz"

const httpClient = axios.create();
httpClient.defaults.timeout = 5000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;

export default class RemoteServices {

  static async getQuestions() : Promise<Question[]> {
    return httpClient.get( "/questions")
      .then(response => {
        return response.data as Question[];
      })
      .catch(error => {
        return error;
      });
  }

  static createQuestion(question: Question) {
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

  static uploadImage(file: File, questionId: number) : Promise<string>{
    let formData = new FormData();
    formData.append('file', file);
    return httpClient.put("/questions/" + questionId + "/image",
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );
  }

  static updateQuestionTopics(questionId: number, topics: string[]) {
    return httpClient.put("/questions/" + questionId + "/topics", topics)
  }

  static getTopics() : Promise<string[]>{
    // @ts-ignore
    return httpClient.get("/topics")
        .then(response => {
          return response.data as string[];
        })
        .catch(error => {
          if (error.code === 'ECONNABORTED')
            throw Error('Timeout: Can not connect to server');
        })
  }

  static createTopic(topic: string) {
    return httpClient.post(
      "/topics/",
      topic,
      {headers: {"Content-Type": "text/plain"}}
    );
  }

  static updateTopic(topic: string, newName: string) {
    return httpClient.put(
      "/topics/" + topic,
      newName,
      {headers: {"Content-Type": "text/plain"}}
    );
  }

  static deleteTopic(topic: string) {
    return httpClient.delete("/topics/" + topic);
  }

  static async getNonGeneratedQuizzes() : Promise<Quiz[]> {
    return httpClient.get("/quizzes/nongenerated")
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

