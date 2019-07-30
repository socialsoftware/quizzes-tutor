import axios from "axios";

interface StatsDto {
  totalQuizzes: number;
  totalAnswers: number;
  uniqueCorrectAnswers: number;
  uniqueWrongAnswers: number;
  totalUniqueQuestions: number;
}

export default class Stats implements StatsDto {
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
