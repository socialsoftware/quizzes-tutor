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
import { Student } from '@/models/management/Student';
import Assessment from '@/models/management/Assessment';
import AuthDto from '@/models/user/AuthDto';
import { QuizAnswers } from '@/models/management/QuizAnswers';
const httpClient = axios.create();
httpClient.defaults.timeout = 100000;
httpClient.defaults.baseURL =
    process.env.VUE_APP_ROOT_API || 'http://localhost:8080';
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(config => {
    if (!config.headers.Authorization) {
        const token = Store.getters.getToken;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }
    return config;
}, error => Promise.reject(error));
export default class RemoteServices {
    static async fenixLogin(code) {
        return httpClient
            .get(`/auth/fenix?code=${code}`)
            .then(response => {
            return new AuthDto(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async demoStudentLogin() {
        return httpClient
            .get('/auth/demo/student')
            .then(response => {
            return new AuthDto(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async demoTeacherLogin() {
        return httpClient
            .get('/auth/demo/teacher')
            .then(response => {
            return new AuthDto(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async demoAdminLogin() {
        return httpClient
            .get('/auth/demo/admin')
            .then(response => {
            return new AuthDto(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getUserStats() {
        return httpClient
            .get(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats`)
            .then(response => {
            return new StudentStats(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getQuestions() {
        return httpClient
            .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions`)
            .then(response => {
            return response.data.map((question) => {
                return new Question(question);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async exportCourseQuestions() {
        return httpClient
            .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions/export`, {
            responseType: 'blob'
        })
            .then(response => {
            return new Blob([response.data], {
                type: 'application/zip, application/octet-stream'
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getAvailableQuestions() {
        return httpClient
            .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions/available`)
            .then(response => {
            return response.data.map((question) => {
                return new Question(question);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async createQuestion(question) {
        return httpClient
            .post(`/courses/${Store.getters.getCurrentCourse.courseId}/questions/`, question)
            .then(response => {
            return new Question(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async updateQuestion(question) {
        return httpClient
            .put(`/questions/${question.id}`, question)
            .then(response => {
            return new Question(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async deleteQuestion(questionId) {
        return httpClient.delete(`/questions/${questionId}`).catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async setQuestionStatus(questionId, status) {
        return httpClient
            .post(`/questions/${questionId}/set-status`, status, {})
            .then(response => {
            return new Question(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async uploadImage(file, questionId) {
        let formData = new FormData();
        formData.append('file', file);
        return httpClient
            .put(`/questions/${questionId}/image`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
            .then(response => {
            return response.data;
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async updateQuestionTopics(questionId, topics) {
        return httpClient.put(`/questions/${questionId}/topics`, topics);
    }
    static async getTopics() {
        return httpClient
            .get(`/courses/${Store.getters.getCurrentCourse.courseId}/topics`)
            .then(response => {
            return response.data.map((topic) => {
                return new Topic(topic);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getAvailableQuizzes() {
        return httpClient
            .get(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/available`)
            .then(response => {
            return response.data.map((statementQuiz) => {
                return new StatementQuiz(statementQuiz);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async generateStatementQuiz(params) {
        return httpClient
            .post(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/generate`, params)
            .then(response => {
            return new StatementQuiz(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getSolvedQuizzes() {
        return httpClient
            .get(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/solved`)
            .then(response => {
            return response.data.map((solvedQuiz) => {
                return new SolvedQuiz(solvedQuiz);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getQuizByQRCode(quizId) {
        return httpClient
            .get(`/quizzes/${quizId}/byqrcode`)
            .then(response => {
            return new StatementQuiz(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async exportQuiz(quizId) {
        return httpClient
            .get(`/quizzes/${quizId}/export`, {
            responseType: 'blob'
        })
            .then(response => {
            return new Blob([response.data], {
                type: 'application/zip, application/octet-stream'
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async startQuiz(quizId) {
        return httpClient
            .get(`/quizzes/${quizId}/start`)
            .then(response => {
            return new StatementQuiz(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static submitAnswer(quizId, answer) {
        httpClient.post(`/quizzes/${quizId}/submit`, answer).catch(error => {
            console.debug(error);
        });
    }
    static async concludeQuiz(statementQuiz) {
        let sendStatement = { ...statementQuiz, questions: [] };
        return httpClient
            .post(`/quizzes/${statementQuiz.id}/conclude`, sendStatement)
            .then(response => {
            if (response.data) {
                return response.data.map((answer) => {
                    return new StatementCorrectAnswer(answer);
                });
            }
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async createTopic(topic) {
        return httpClient
            .post(`/courses/${Store.getters.getCurrentCourse.courseId}/topics/`, topic)
            .then(response => {
            return new Topic(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async updateTopic(topic) {
        return httpClient
            .put(`/topics/${topic.id}`, topic)
            .then(response => {
            return new Topic(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async deleteTopic(topic) {
        return httpClient.delete(`/topics/${topic.id}`).catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getNonGeneratedQuizzes() {
        return httpClient
            .get(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/non-generated`)
            .then(response => {
            return response.data.map((quiz) => {
                return new Quiz(quiz);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async deleteQuiz(quizId) {
        return httpClient.delete(`/quizzes/${quizId}`).catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getQuiz(quizId) {
        return httpClient
            .get(`/quizzes/${quizId}`)
            .then(response => {
            return new Quiz(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getQuizAnswers(quizId) {
        return httpClient
            .get(`/quizzes/${quizId}/answers`)
            .then(response => {
            return new QuizAnswers(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async saveQuiz(quiz) {
        if (quiz.id) {
            return httpClient
                .put(`/quizzes/${quiz.id}`, quiz)
                .then(response => {
                return new Quiz(response.data);
            })
                .catch(async (error) => {
                throw Error(await this.errorMessage(error));
            });
        }
        else {
            return httpClient
                .post(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes`, quiz)
                .then(response => {
                return new Quiz(response.data);
            })
                .catch(async (error) => {
                throw Error(await this.errorMessage(error));
            });
        }
    }
    static async removeNonAnsweredQuizAnswers(quizId) {
        return httpClient
            .post(`/quizzes/${quizId}/unpopulate`)
            .then(response => {
            return new Quiz(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async populateWithQuizAnswers(quizId) {
        return httpClient
            .post(`/quizzes/${quizId}/populate`)
            .then(response => {
            return new Quiz(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getCourseStudents(course) {
        return httpClient
            .get(`/executions/${course.courseExecutionId}/students`)
            .then(response => {
            return response.data.map((student) => {
                return new Student(student);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getAssessments() {
        return httpClient
            .get(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`)
            .then(response => {
            return response.data.map((assessment) => {
                return new Assessment(assessment);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async getAvailableAssessments() {
        return httpClient
            .get(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments/available`)
            .then(response => {
            return response.data.map((assessment) => {
                return new Assessment(assessment);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async saveAssessment(assessment) {
        if (assessment.id) {
            return httpClient
                .put(`/assessments/${assessment.id}`, assessment)
                .then(response => {
                return new Assessment(response.data);
            })
                .catch(async (error) => {
                throw Error(await this.errorMessage(error));
            });
        }
        else {
            return httpClient
                .post(`/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`, assessment)
                .then(response => {
                return new Assessment(response.data);
            })
                .catch(async (error) => {
                throw Error(await this.errorMessage(error));
            });
        }
    }
    static async deleteAssessment(assessmentId) {
        return httpClient
            .delete(`/assessments/${assessmentId}`)
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async setAssessmentStatus(assessmentId, status) {
        return httpClient
            .post(`/assessments/${assessmentId}/set-status`, status, {
            headers: {
                'Content-Type': 'text/html'
            }
        })
            .then(response => {
            return new Assessment(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static getCourses() {
        return httpClient
            .get('/courses/executions')
            .then(response => {
            return response.data.map((course) => {
                return new Course(course);
            });
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async activateCourse(course) {
        return httpClient
            .post('/courses/activate', course)
            .then(response => {
            return new Course(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async createExternalCourse(course) {
        return httpClient
            .post('/courses/external', course)
            .then(response => {
            return new Course(response.data);
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async deleteCourse(courseExecutionId) {
        return httpClient
            .delete(`/executions/${courseExecutionId}`)
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async exportAll() {
        return httpClient
            .get('/admin/export', {
            responseType: 'blob'
        })
            .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            let dateTime = new Date();
            link.setAttribute('download', `export-${dateTime.toLocaleString()}.zip`);
            document.body.appendChild(link);
            link.click();
        })
            .catch(async (error) => {
            throw Error(await this.errorMessage(error));
        });
    }
    static async errorMessage(error) {
        if (error.message === 'Network Error') {
            return 'Unable to connect to server';
        }
        else if (error.message.split(' ')[0] === 'timeout') {
            return 'Request timeout - Server took too long to respond';
        }
        else if (error.response) {
            return error.response.data.message;
        }
        else if (error.message === 'Request failed with status code 403') {
            await Store.dispatch('logout');
            return 'Unauthorized access or Expired token';
        }
        else {
            console.log(error);
            return 'Unknown Error - Contact admin';
        }
    }
}
//# sourceMappingURL=RemoteServices.js.map