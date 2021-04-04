import axios from 'axios';
import Store from '@/store';
import Question from '@/models/management/Question';
import { Quiz } from '@/models/management/Quiz';
import Course from '@/models/user/Course';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import StudentStats from '@/models/statement/StudentStats';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import Topic from '@/models/management/Topic';
import { Student } from '@/models/user/Student';
import Assessment from '@/models/management/Assessment';
import AuthDto from '@/models/user/AuthDto';
import ExternalUser from '@/models/user/ExternalUser';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import Discussion from '@/models/management/Discussion';
import Reply from '@/models/management/Reply';
import Tournament from '@/models/user/Tournament';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Review from '@/models/management/Review';
import UserQuestionSubmissionInfo from '@/models/management/UserQuestionSubmissionInfo';
import StatementQuestion from '@/models/statement/StatementQuestion';
import router from '@/router';

const httpClient = axios.create();
httpClient.defaults.timeout = 100000;
httpClient.defaults.baseURL =
  process.env.VUE_APP_ROOT_API || 'http://localhost:8080';
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(
  (config) => {
    if (!config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);
httpClient.interceptors.response.use(
  (response) => {
    if (response.data.notification) {
      if (response.data.notification.errorMessages.length)
        Store.dispatch(
          'notification',
          response.data.notification.errorMessages
        );
      response.data = response.data.response;
    }
    return response;
  },
  (error) => Promise.reject(error)
);

export default class RemoteServices {
  // AuthUser Controller

  static async fenixLogin(code: string): Promise<AuthDto> {
    return httpClient
      .get(`/auth/fenix?code=${code}`)
      .then((response) => {
        return new AuthDto(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async externalLogin(
    email: string,
    password: string
  ): Promise<AuthDto> {
    return httpClient
      .get(`/auth/external?email=${email}&password=${password}`)
      .then((response) => {
        return new AuthDto(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoStudentLogin(createNew: boolean): Promise<AuthDto> {
    return httpClient
      .get(`/auth/demo/student?createNew=${createNew}`)
      .then((response) => {
        return new AuthDto(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoTeacherLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/teacher')
      .then((response) => {
        return new AuthDto(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoAdminLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/admin')
      .then((response) => {
        return new AuthDto(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // User Controller

  static async registerExternalUser(
    executionId: number,
    externalUser: ExternalUser
  ): Promise<ExternalUser> {
    return httpClient
      .post(`/users/register/${executionId}`, externalUser)
      .then((response) => {
        return new ExternalUser(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async registerExternalUsersCsvFile(
    file: File,
    executionId: number
  ): Promise<Course> {
    const formData = new FormData();
    formData.append('file', file);
    return httpClient
      .post(`/users/register/${executionId}/csv`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      .then((response) => {
        return new Course(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async confirmRegistration(
    externalUser: ExternalUser
  ): Promise<ExternalUser> {
    return httpClient
      .post('/users/register/confirm', externalUser)
      .then((response) => {
        return new ExternalUser(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Statistics Controller

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats`
      )
      .then((response) => {
        return new StudentStats(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Questions Controller

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions`)
      .then((response) => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportCourseQuestions(): Promise<Blob> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/export`,
        {
          responseType: 'blob',
        }
      )
      .then((response) => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream',
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async importQuestions(file: File): Promise<Question[]> {
    const formData = new FormData();
    formData.append('file', file);
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/import/questions`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      )
      .then((response) => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuestions(): Promise<Question[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/available`
      )
      .then((response) => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/`,
        question
      )
      .then((response) => {
        return new Question(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestion(question: Question): Promise<Question> {
    return httpClient
      .put(`/questions/${question.id}`, question)
      .then((response) => {
        return new Question(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuestion(questionId: number) {
    return httpClient
      .delete(`/questions/${questionId}`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setQuestionStatus(
    questionId: number,
    status: String
  ): Promise<Question> {
    return httpClient
      .post(`/questions/${questionId}/set-status`, status, {})
      .then((response) => {
        return new Question(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadImage(file: File, questionId: number): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    return httpClient
      .put(`/questions/${questionId}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
      .then((response) => {
        return response.data as string;
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionTopics(questionId: number, topics: Topic[]) {
    return httpClient.put(`/questions/${questionId}/topics`, topics);
  }

  // Topics Controller

  static async getTopics(): Promise<Topic[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/topics`)
      .then((response) => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/topics/`,
        topic
      )
      .then((response) => {
        return new Topic(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .put(`/topics/${topic.id}`, topic)
      .then((response) => {
        return new Topic(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteTopic(topic: Topic) {
    return httpClient.delete(`/topics/${topic.id}`).catch(async (error) => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getTopicQuestions(topicId: number): Promise<Question[]> {
    return httpClient
      .get(`/topics/${topicId}/questions`)
      .then((response) => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // CourseExecution Controller

  static async getCourses(): Promise<Course[]> {
    return httpClient
      .get('/executions')
      .then((response) => {
        return response.data.map((course: any) => {
          return new Course(course);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createExternalCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/executions/external', course)
      .then((response) => {
        return new Course(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteCourseExecution(courseExecutionId: number | undefined) {
    return httpClient
      .delete(`/executions/${courseExecutionId}`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async activateCourseExecution(course: Course): Promise<Course> {
    return httpClient
      .post('/executions/activate', course)
      .then((response) => {
        return new Course(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async anonymizeCourse(courseExecutionId: number | undefined) {
    return httpClient
      .get(`/executions/${courseExecutionId}/anonymize`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getCourseStudents(course: Course) {
    return httpClient
      .get(`/executions/${course.courseExecutionId}/students`)
      .then((response) => {
        return response.data.map((student: any) => {
          return new Student(student);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteExternalInactiveUsers(
    courseExecution: Course,
    userIdList: number[]
  ): Promise<Course> {
    return httpClient
      .post(
        `/executions/${courseExecution.courseExecutionId}/users/delete`,
        userIdList
      )
      .then((response) => {
        return new Course(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportCourseExecutionInfo(executionId: number): Promise<Blob> {
    return httpClient
      .get(`/executions/${executionId}/export`, {
        responseType: 'blob',
      })
      .then((response) => {
        return new Blob([response.data], {
          type: 'application/tar.gz, application/octet-stream',
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableTopicsByCourseExecution(): Promise<Topic[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/topics/available`
      )
      .then((response) => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Assessment Controller

  static async getAssessments(): Promise<Assessment[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`
      )
      .then((response) => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableAssessments() {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments/available`
      )
      .then((response) => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveAssessment(assessment: Assessment) {
    if (assessment.id) {
      return httpClient
        .put(`/assessments/${assessment.id}`, assessment)
        .then((response) => {
          return new Assessment(response.data);
        })
        .catch(async (error) => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`,
          assessment
        )
        .then((response) => {
          return new Assessment(response.data);
        })
        .catch(async (error) => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteAssessment(assessmentId: number) {
    return httpClient
      .delete(`/assessments/${assessmentId}`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setAssessmentStatus(
    assessmentId: number,
    status: string
  ): Promise<Assessment> {
    return httpClient
      .post(`/assessments/${assessmentId}/set-status`, status, {
        headers: {
          'Content-Type': 'text/html',
        },
      })
      .then((response) => {
        return new Assessment(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAssessmentQuestions(assessmentId: number) {
    return httpClient
      .get(`/assessments/${assessmentId}/questions`)
      .then((response) => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Quiz Controller

  static async getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/non-generated`
      )
      .then((response) => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get(`/quizzes/${quizId}`)
      .then((response) => {
        return new Quiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put(`/quizzes/${quiz.id}`, quiz)
        .then((response) => {
          return new Quiz(response.data);
        })
        .catch(async (error) => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes`,
          quiz
        )
        .then((response) => {
          return new Quiz(response.data);
        })
        .catch(async (error) => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteQuiz(quizId: number) {
    return httpClient.delete(`/quizzes/${quizId}`).catch(async (error) => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async exportQuiz(quizId: number): Promise<Blob> {
    return httpClient
      .get(`/quizzes/${quizId}/export`, {
        responseType: 'blob',
      })
      .then((response) => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream',
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async populateWithQuizAnswers(quizId: number): Promise<Quiz> {
    return httpClient
      .post(`/quizzes/${quizId}/populate`)
      .then((response) => {
        return new Quiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async removeNonAnsweredQuizAnswers(quizId: number): Promise<Quiz> {
    return httpClient
      .post(`/quizzes/${quizId}/unpopulate`)
      .then((response) => {
        return new Quiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizAnswers(quizId: number): Promise<QuizAnswers> {
    return httpClient
      .get(`/quizzes/${quizId}/answers`)
      .then((response) => {
        return new QuizAnswers(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Answer Controller

  static async getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/available`
      )
      .then((response) => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async generateStatementQuiz(params: object): Promise<StatementQuiz> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/generate`,
        params
      )
      .then((response) => {
        return new StatementQuiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/solved`
      )
      .then((response) => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizByQRCode(quizId: number): Promise<StatementQuiz> {
    return httpClient
      .get(`/quizzes/${quizId}/byqrcode`)
      .then((response) => {
        return new StatementQuiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async startQuiz(quizId: number): Promise<StatementQuiz> {
    return httpClient
      .get(`/quizzes/${quizId}/start`)
      .then((response) => {
        return new StatementQuiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestion(
    quizId: number,
    questionId: number
  ): Promise<StatementQuestion> {
    return httpClient
      .get(`/quizzes/${quizId}/question/${questionId}`)
      .then((response) => {
        return new StatementQuestion(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static submitAnswer(quizId: number, answer: StatementAnswer) {
    httpClient.post(`/quizzes/${quizId}/submit`, answer).catch((error) => {
      console.debug(error);
    });
  }

  static async concludeQuiz(
    statementQuiz: StatementQuiz
  ): Promise<StatementCorrectAnswer[]> {
    const sendStatement = { ...statementQuiz, questions: [] };
    return httpClient
      .post(`/quizzes/${statementQuiz.id}/conclude`, sendStatement)
      .then((response) => {
        if (response.data) {
          return response.data.map((answer: any) => {
            return new StatementCorrectAnswer(answer);
          });
        }
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // ImportExport Controller

  static async exportAll() {
    return httpClient
      .get('/admin/export', {
        timeout: 1000000,
        responseType: 'blob',
      })
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        const dateTime = new Date();
        link.setAttribute(
          'download',
          `export-${dateTime.toLocaleString()}.zip`
        );
        document.body.appendChild(link);
        link.click();
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Question Submission Controller

  static async createQuestionSubmission(
    questionSubmission: QuestionSubmission
  ): Promise<QuestionSubmission> {
    return httpClient
      .post(
        `/submissions/${Store.getters.getCurrentCourse.courseExecutionId}`,
        questionSubmission
      )
      .then((response) => {
        return new QuestionSubmission(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionSubmission(
    questionSubmission: QuestionSubmission
  ): Promise<QuestionSubmission> {
    return httpClient
      .put(`/submissions/${questionSubmission.id}`, questionSubmission)
      .then((response) => {
        return new QuestionSubmission(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteSubmittedQuestion(questionSubmissionId: number) {
    return httpClient
      .delete(`/submissions/${questionSubmissionId}`)
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createReview(review: Review): Promise<Review> {
    return httpClient
      .post(`/submissions/${review.questionSubmissionId}/reviews`, review)
      .then((response) => {
        return new Review(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionSubmissionTopics(
    questionSubmissionId: number,
    topics: Topic[]
  ) {
    return httpClient.put(
      `/submissions/${questionSubmissionId}/topics`,
      topics
    );
  }

  static async getStudentQuestionSubmissions(): Promise<QuestionSubmission[]> {
    return httpClient
      .get(
        `/submissions/${Store.getters.getCurrentCourse.courseExecutionId}/student`
      )
      .then((response) => {
        return response.data.map((questionSubmission: any) => {
          return new QuestionSubmission(questionSubmission);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getCourseExecutionQuestionSubmissions(): Promise<
    QuestionSubmission[]
  > {
    return httpClient
      .get(
        `/submissions/${Store.getters.getCurrentCourse.courseExecutionId}/execution`
      )
      .then((response) => {
        return response.data.map((questionSubmission: any) => {
          return new QuestionSubmission(questionSubmission);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestionSubmissionReviews(
    questionSubmissionId: number
  ): Promise<Review[]> {
    return httpClient
      .get(`/submissions/${questionSubmissionId}/reviews`)
      .then((response) => {
        return response.data.map((review: any) => {
          return new Review(review);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAllStudentsSubmissionsInfo(): Promise<
    UserQuestionSubmissionInfo[]
  > {
    return httpClient
      .get(
        `/submissions/${Store.getters.getCurrentCourse.courseExecutionId}/all`
      )
      .then((response) => {
        return response.data.map((userSubmissionsInfo: any) => {
          return new UserQuestionSubmissionInfo(userSubmissionsInfo);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async toggleStudentNotificationRead(
    questionSubmissionId: number,
    hasRead: boolean
  ) {
    return httpClient
      .put(
        `/submissions/${questionSubmissionId}/toggle-notification-student?hasRead=${hasRead}`
      )
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async toggleTeacherNotificationRead(
    questionSubmissionId: number,
    hasRead: boolean
  ) {
    return httpClient
      .put(
        `/submissions/${questionSubmissionId}/toggle-notification-teacher?hasRead=${hasRead}`
      )
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Tournament Controller

  static async createTournament(
    topicsId: Number[],
    tournament: Tournament
  ): Promise<Tournament> {
    let path: string = `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}?`;
    for (const topicId of topicsId) {
      path += 'topicsId=' + topicId + '&';
    }
    path = path.substring(0, path.length - 1);
    return httpClient
      .post(path, tournament)
      .then((response) => {
        return new Tournament(response.data, Store.getters.getUser);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getTournamentsForCourseExecution(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/getTournaments`
      )
      .then((response) => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament, Store.getters.getUser);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getOpenedTournamentsForCourseExecution(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/getOpenTournaments`
      )
      .then((response) => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament, Store.getters.getUser);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getClosedTournamentsForCourseExecution(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/getClosedTournaments`
      )
      .then((response) => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament, Store.getters.getUser);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getTournament(tournamentId: number): Promise<Tournament> {
    return httpClient
      .get(
        `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/tournament/${tournamentId}`
      )
      .then((response) => {
        return new Tournament(response.data, Store.getters.getUser);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static joinTournament(tournamentId: number, password: String) {
    return httpClient
      .put(
        `tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/joinTournament/${tournamentId}?password=` +
          password
      )
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static solveTournament(tournamentId: number): Promise<StatementQuiz> {
    return httpClient
      .put(
        `tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/solveQuiz/${tournamentId}`
      )
      .then((response) => {
        return new StatementQuiz(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static leaveTournament(tournamentId: number) {
    return httpClient
      .put(
        `tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/leaveTournament/${tournamentId}`
      )
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateTournament(
    topicsId: Number[],
    tournament: Tournament
  ): Promise<Tournament> {
    let path: string = `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/updateTournament?`;
    for (const topicId of topicsId) {
      path += 'topicsId=' + topicId + '&';
    }
    path = path.substring(0, path.length - 1);
    return httpClient
      .put(path, tournament)
      .then((response) => {
        return new Tournament(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static cancelTournament(tournamentId: number) {
    return httpClient
      .put(
        `tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/cancelTournament/${tournamentId}`
      )
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static removeTournament(tournamentId: number) {
    return httpClient
      .delete(
        `/tournaments/${Store.getters.getCurrentCourse.courseExecutionId}/removeTournament/${tournamentId}`
      )
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Discussion Controller

  static async getCourseExecutionDiscussions(): Promise<Discussion[]> {
    return httpClient
      .get(
        `/discussions/courseexecutions/${Store.getters.getCurrentCourse.courseExecutionId}`
      )
      .then((response) => {
        return response.data.map((discussion: any) => {
          return new Discussion(discussion);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getOpenCourseExecutionDiscussions(): Promise<Discussion[]> {
    return httpClient
      .get(
        `/discussions/open/courseexecutions/${Store.getters.getCurrentCourse.courseExecutionId}`
      )
      .then((response) => {
        return response.data.map((discussion: any) => {
          return new Discussion(discussion);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeDiscussionStatus(id: number): Promise<Discussion> {
    return httpClient
      .put('/discussions/' + id + '/status')
      .then((response) => {
        return new Discussion(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getClarificationsByQuestionId(id: number): Promise<Reply[]> {
    return httpClient
      .get('/discussions/clarifications/questions/' + id)
      .then((response) => {
        return response.data.map((reply: any) => {
          return new Reply(reply);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserDiscussions(): Promise<Discussion[]> {
    return httpClient
      .get(
        `/discussions/courseexecutions/${Store.getters.getCurrentCourse.courseExecutionId}/users`
      )
      .then((response) => {
        return response.data.map((discussion: any) => {
          return new Discussion(discussion);
        });
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async addReply(reply: Reply, discussionId: number): Promise<Reply> {
    return httpClient
      .post('/discussions/' + discussionId + '/replies/add', reply)
      .then((response) => {
        return new Reply(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeReplyAvailability(id: number): Promise<Discussion> {
    return httpClient
      .put('/discussions/replies/' + id + '/availability')
      .then((response) => {
        return new Discussion(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createDiscussion(
    discussion: Discussion,
    questionAnswerId: number
  ): Promise<Discussion> {
    return httpClient
      .post(
        `/discussions/create?questionAnswerId=${questionAnswerId}`,
        discussion
      )
      .then((response) => {
        return new Discussion(response.data);
      })
      .catch(async (error) => {
        throw Error(await this.errorMessage(error));
      });
  }

  // Error

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to server';
    } else if (error.message === 'Request failed with status code 403') {
      await Store.dispatch('logout');
      await router.push({ path: '/' });
      return 'Unauthorized access or expired token';
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond';
    } else if (error.response) {
      return error.response.data.message;
    } else {
      console.log(error);
      return 'Unknown Error - Contact admin';
    }
  }
}
