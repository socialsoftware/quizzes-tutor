import axios from "axios";
import Store from "@/store";
import Question from "@/models/management/Question";
import { Quiz } from "@/models/management/Quiz";
import Course from "@/models/auth/Course";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import StudentStats from "@/models/statement/StudentStats";
import StatementQuiz from "@/models/statement/StatementQuiz";
import SolvedQuiz from "@/models/statement/SolvedQuiz";
import Topic from "@/models/management/Topic";
import { Student } from "@/models/management/Student";
import Assessment from "@/models/management/Assessment";
import AuthDto from "@/models/auth/AuthDto";

const httpClient = axios.create();
httpClient.defaults.timeout = 10000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;
httpClient.defaults.headers.post["Content-Type"] = "application/json";
httpClient.interceptors.request.use(
  config => {
    if (!config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  error => Promise.reject(error)
);

export default class RemoteServices {
  static async authenticate(code: string): Promise<AuthDto> {
    return httpClient
      .post("/auth/fenix", { code: code })
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get("/stats")
      .then(response => {
        return new StudentStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get("/courses/" + Store.getters.getCurrentCourse.name + "/questions")
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuestions(): Promise<Question[]> {
    return httpClient
      .get(
        "/courses/" +
          Store.getters.getCurrentCourse.name +
          "/questions/available"
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post(
        "/courses/" + Store.getters.getCurrentCourse.name + "/questions/",
        question
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static updateQuestion(question: Question): Promise<Question> {
    return httpClient
      .put("/questions/" + question.id, question)
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static deleteQuestion(questionId: number) {
    return httpClient.delete("/questions/" + questionId).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static setQuestionStatus(
    questionId: number,
    status: String
  ): Promise<Question> {
    return httpClient
      .post("/questions/" + questionId + "/set-status", status, {})
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append("file", file);
    return httpClient
      .put("/questions/" + questionId + "/image", formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static updateQuestionTopics(questionId: number, topics: Topic[]) {
    return httpClient.put("/questions/" + questionId + "/topics", topics);
  }

  static getTopics(): Promise<Topic[]> {
    return httpClient
      .get("/courses/" + Store.getters.getCurrentCourse.name + "/topics")
      .then(response => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getQuizStatement(params: object): Promise<StatementQuiz> {
    return httpClient
      .post("/student/quizzes/generate", params)
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get("/student/quizzes/available")
      .then(response => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get("/student/quizzes/solved")
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getCorrectAnswers(params: object): Promise<StatementCorrectAnswer[]> {
    return httpClient
      .post("/student/quizzes/answer", params)
      .then(response => {
        return response.data.answers.map((answer: any) => {
          return new StatementCorrectAnswer(answer);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static createTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .post(
        "/courses/" + Store.getters.getCurrentCourse.name + "/topics/",
        topic
      )
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static updateTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .put("/topics/" + topic.id, topic)
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static deleteTopic(topic: Topic) {
    return httpClient.delete("/topics/" + topic.id).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get("/quizzes/non-generated")
      .then(response => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuiz(quizId: number) {
    return httpClient.delete("/quizzes/" + quizId).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get("/quizzes/" + quizId)
      .then(response => {
        return new Quiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put("/quizzes/" + quiz.id, quiz)
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post("/quizzes/", quiz)
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async getCourses(): Promise<Course[]> {
    return httpClient
      .get("/courses/" + Store.getters.getCurrentCourse.name + "/executions")
      .then(response => {
        return response.data.map((course: any) => {
          return new Course(course);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getCourseStudents(course: Course) {
    return httpClient
      .get(
        "/courses/" +
          course.name +
          "/executions/" +
          course.acronym +
          "/" +
          course.academicTerm.replace("/", "_") +
          "/students"
      )
      .then(response => {
        return response.data.map((student: any) => {
          return new Student(student);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getAssessments(): Promise<Assessment[]> {
    return httpClient
      .get("/assessments")
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableAssessments() {
    return httpClient
      .get("/assessments/available")
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveAssessment(assessment: Assessment) {
    if (assessment.id) {
      return httpClient
        .put("/assessments/" + assessment.id, assessment)
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post("/assessments/", assessment)
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteAssessment(assessmentId: number) {
    return httpClient
      .delete("/assessments/" + assessmentId)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setAssessmentStatus(
    assessmentId: number,
    status: string
  ): Promise<Assessment> {
    return httpClient
      .post("/assessments/" + assessmentId + "/set-status", status, {
        headers: {
          "Content-Type": "text/html"
        }
      })
      .then(response => {
        return new Assessment(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportAll() {
    return httpClient
      .get("/admin/export", {
        headers: {
          Authorization: Store.getters.getToken
        },
        responseType: "blob"
      })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        let dateTime = new Date();
        link.setAttribute(
          "download",
          "export-" + dateTime.toLocaleString() + ".zip"
        );
        document.body.appendChild(link);
        link.click();
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === "Network Error") {
      return "Unable to connect to server";
    } else if (error.message.split(" ")[0] === "timeout") {
      return "Request timeout - Server took too long to respond";
    } else if (error.response) {
      return error.response.data.message;
    } else if (error.message === "Request failed with status code 403") {
      Store.dispatch("logout");
      return "Unauthorized access or Expired token";
    } else {
      return "Unknown Error - Contact admin";
    }
  }
}
