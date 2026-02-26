import { defineStore } from 'pinia';
import RemoteServices from '@/services/RemoteServices';
import AuthDto from '@/models/user/AuthDto';
import Course from '@/models/user/Course';
import AuthUser from '@/models/user/AuthUser';
import ExternalUser from '@/models/user/ExternalUser';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

interface State {
  token: string;
  user: AuthUser | null;
  currentCourse: Course | null;
  statementQuiz: StatementQuiz | null;
  correctAnswers: StatementCorrectAnswer[];
  error: boolean;
  errorMessage: string;
  notification: boolean;
  notificationMessageList: string[];
  loading: boolean;
}

export const useStore = defineStore('main', {
  state: (): State => ({
    token: '',
    user: null,
    currentCourse: null,
    statementQuiz: null,
    correctAnswers: [],
    error: false,
    errorMessage: '',
    notification: false,
    notificationMessageList: [],
    loading: false,
  }),
  getters: {
    isLoggedIn(state): boolean { return !!state.token; },
    isAdmin(state): boolean {
      return !!state.token && state.user !== null && (state.user.admin || state.user.role == 'DEMO_ADMIN');
    },
    isTeacher(state): boolean {
      return !!state.token && state.user !== null && state.user.role == 'TEACHER';
    },
    isStudent(state): boolean {
      return !!state.token && state.user !== null && state.user.role == 'STUDENT';
    },
    getToken(state): string { return state.token; },
    getUser(state): AuthUser | null { return state.user; },
    getCurrentCourse(state): Course | null { return state.currentCourse; },
    getStatementQuiz(state): StatementQuiz | null { return state.statementQuiz; },
    getCorrectAnswers(state): StatementCorrectAnswer[] { return state.correctAnswers; },
    getError(state): boolean { return state.error; },
    getErrorMessage(state): string { return state.errorMessage; },
    getNotification(state): boolean { return state.notification; },
    getNotificationMessageList(state): string[] { return state.notificationMessageList; },
    getLoading(state): boolean { return state.loading; },
  },
  actions: {
    initialiseStore() {
      const token = localStorage.getItem('token');
      if (token) this.token = token;
      const user = localStorage.getItem('user');
      if (user) { try { this.user = JSON.parse(user); } catch (e) { } }
      const currentCourse = localStorage.getItem('currentCourse');
      if (currentCourse) { try { this.currentCourse = JSON.parse(currentCourse); } catch (e) { } }
    },
    loginMut(authResponse: AuthDto) {
      localStorage.setItem('token', authResponse.token);
      this.token = authResponse.token;
      localStorage.setItem('user', JSON.stringify(authResponse.user));
      this.user = authResponse.user;
      localStorage.setItem('currentCourse', '');
      this.currentCourse = null;
      localStorage.setItem('statementQuiz', '');
      this.statementQuiz = null;
      localStorage.setItem('correctAnswers', '');
      this.correctAnswers = [];
    },
    logoutMut() {
      localStorage.setItem('token', '');
      this.token = '';
      localStorage.setItem('user', '');
      this.user = null;
      localStorage.setItem('currentCourse', '');
      this.currentCourse = null;
      localStorage.setItem('statementQuiz', '');
      this.statementQuiz = null;
      localStorage.setItem('correctAnswers', '');
      this.correctAnswers = [];
    },
    setError(errorMessage: string) {
      this.error = true;
      this.errorMessage = errorMessage;
    },
    clearError() {
      this.error = false;
      this.errorMessage = '';
    },
    setNotification(notificationMessageList: string[]) {
      this.notification = true;
      this.notificationMessageList = notificationMessageList;
    },
    clearNotification() {
      this.notification = false;
      this.notificationMessageList = [];
    },
    setLoading() { this.loading = true; },
    clearLoading() { this.loading = false; },
    setCurrentCourse(currentCourse: Course) {
      localStorage.setItem('currentCourse', JSON.stringify(currentCourse));
      this.currentCourse = currentCourse;
    },
    setStatementQuiz(statementQuiz: StatementQuiz) { this.statementQuiz = statementQuiz; },
    setCorrectAnswers(correctAnswers: StatementCorrectAnswer[]) { this.correctAnswers = correctAnswers; },

    // Async actions
    async fenixLogin(code: string) {
      const authResponse = await RemoteServices.fenixLogin(code);
      this.loginMut(authResponse);
    },
    async externalLogin(user: ExternalUser) {
      const authResponse = await RemoteServices.externalLogin(user.username, user.password);
      this.loginMut(authResponse);
    },
    async demoStudentLogin() {
      const authResponse = await RemoteServices.demoStudentLogin(false);
      this.loginMut(authResponse);
      const courses = Object.values(authResponse.user.courses)[0] as Course[];
      if (courses && courses.length > 0) this.setCurrentCourse(courses[0]);
    },
    async demoNewStudentLogin() {
      const authResponse = await RemoteServices.demoStudentLogin(true);
      this.loginMut(authResponse);
      const courses = Object.values(authResponse.user.courses)[0] as Course[];
      if (courses && courses.length > 0) this.setCurrentCourse(courses[0]);
    },
    async demoTeacherLogin() {
      const authResponse = await RemoteServices.demoTeacherLogin();
      this.loginMut(authResponse);
      const courses = Object.values(authResponse.user.courses)[0] as Course[];
      if (courses && courses.length > 0) this.setCurrentCourse(courses[0]);
    },
    async demoAdminLogin() {
      const authResponse = await RemoteServices.demoAdminLogin();
      this.loginMut(authResponse);
    },
    async logout() {
      this.logoutMut();
    }
  },
});
