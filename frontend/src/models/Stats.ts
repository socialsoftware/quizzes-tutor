import axios from "axios";
import Question from "./Question";
import CorrectAnswer from "@/models/CorrectAnswer";
import Answer from "@/models/Answer";
import Option from "@/models/Option";

interface ServerStats {
  totalQuizzes: number;
  totalAnswers: number;
  uniqueCorrectAnswers: number;
  uniqueWrongAnswers: number;
  totalUniqueQuestions: number;
}

export default class Stats implements ServerStats {
  totalAnswers!: number;
  totalQuizzes!: number;
  totalUniqueQuestions!: number;
  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;

  static async getStats() {
    return await axios
      .get(process.env.VUE_APP_ROOT_API + "/stats")
      .then(response => {
        return response.data as Stats;
      });
  }
}
