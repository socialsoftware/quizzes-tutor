import axios from "axios";

export default class RemoteServices {
    static getQuestions() {
        return axios
          .get(process.env.VUE_APP_ROOT_API + "/questions")
      }
}
