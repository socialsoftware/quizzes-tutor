import axios from "axios";
import { Question } from "@/models/question/Question"

export default class RemoteServices {
  static getQuestions() {
    return axios.get(process.env.VUE_APP_ROOT_API + "/questions");
  }

  static createQuestion(question: Question) {
    return axios.post(process.env.VUE_APP_ROOT_API + "/questions/", question);
  }

  static updateQuestion(questionId: number, question: Question) {
    return axios.put(process.env.VUE_APP_ROOT_API + "/questions/" + questionId, question);
  }

  static deleteQuestion(questionId: number) {
    return axios.delete(process.env.VUE_APP_ROOT_API + "/questions/" + questionId);
  }

  static questionSwitchActive(questionId: number) {
    return axios.put(process.env.VUE_APP_ROOT_API + "/questions/" + questionId + "/switchActive");
  }

  static uploadImage(file: File, questionId: number) {
    let formData = new FormData();
    formData.append('file', file);
    return axios.put(process.env.VUE_APP_ROOT_API + "/questions/" + questionId + "/image", 
      formData, 
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );
  }

  static updateQuestionTopics(questionId: number, topics: string[]) {
    return axios.put(process.env.VUE_APP_ROOT_API + "/questions/" + questionId + "/topics", topics)
  }

  static getTopics() {
    return axios.get(process.env.VUE_APP_ROOT_API + "/topics");
  }
  
  static createTopic(topic: String) {
    return axios.post(
      process.env.VUE_APP_ROOT_API + "/topics/",
      topic,
      {headers: {"Content-Type": "text/plain"}}
    );
  }

}
