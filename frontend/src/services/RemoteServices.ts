import axios from "axios";
import { Question } from "@/models/question/Question"

export default class RemoteServices {
  static getQuestions() {
    return axios.get(process.env.VUE_APP_ROOT_API + "/questions");
  }

  static createQuestion(question: Question) {
    return axios.post(process.env.VUE_APP_ROOT_API + "/questions/", question);
  }

  static updateQuestion(editedId: string, question: Question) {
    return axios.put(process.env.VUE_APP_ROOT_API + "/questions/" + editedId, question);
  }
}
