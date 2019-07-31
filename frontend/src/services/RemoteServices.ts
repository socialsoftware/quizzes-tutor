import axios from "axios";
import QuestionForm from "@/models/question/QuestionForm"

export default class RemoteServices {
  static getQuestions() {
    return axios.get(process.env.VUE_APP_ROOT_API + "/questions");
  }

  static updateQuestion(editedId: string, question: QuestionForm) {
    return axios.put(process.env.VUE_APP_ROOT_API + "/questions/" + editedId, question);
  }
}
