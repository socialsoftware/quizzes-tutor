import Vue from 'vue';
import Vuex from 'vuex';
import RemoteServices from '@/services/RemoteServices';
import AuthDto from '@/models/user/AuthDto';
import Course from '@/models/user/Course';
import AuthUser from '@/models/user/AuthUser';
import ExternalUser from '@/models/user/ExternalUser';

interface State {
  token: string;
  user: AuthUser | null;
  currentCourse: Course | null;
  error: boolean;
  errorMessage: string;
  notification: boolean;
  notificationMessageList: string[];
  loading: boolean;
}

const state: State = {
  token: '',
  user: null,
  currentCourse: null,
  error: false,
  errorMessage: '',
  notification: false,
  notificationMessageList: [],
  loading: false,
};

Vue.use(Vuex);
Vue.config.devtools = true;

export default new Vuex.Store({
  state: state,
  mutations: {
    initialiseStore(state) {
      const token = localStorage.getItem('token');
      if (token) {
        state.token = token;
      }
      const user = localStorage.getItem('user');
      if (user) {
        state.user = JSON.parse(user);
      }
      const currentCourse = localStorage.getItem('currentCourse');
      if (currentCourse) {
        state.currentCourse = JSON.parse(currentCourse);
      }
    },
    login(state, authResponse: AuthDto) {
      localStorage.setItem('token', authResponse.token);
      state.token = authResponse.token;
      localStorage.setItem('user', JSON.stringify(authResponse.user));
      state.user = authResponse.user;
    },
    logout(state) {
      localStorage.setItem('token', '');
      state.token = '';
      localStorage.setItem('user', '');
      state.user = null;
      localStorage.setItem('currentCourse', '');
      state.currentCourse = null;
    },
    error(state, errorMessage: string) {
      state.error = true;
      state.errorMessage = errorMessage;
    },
    clearError(state) {
      state.error = false;
      state.errorMessage = '';
    },
    notification(state, notificationMessageList: string[]) {
      state.notification = true;
      state.notificationMessageList = notificationMessageList;
    },
    clearNotification(state) {
      state.notification = false;
      state.notificationMessageList = [];
    },
    loading(state) {
      state.loading = true;
    },
    clearLoading(state) {
      state.loading = false;
    },
    currentCourse(state, currentCourse: Course) {
      localStorage.setItem('currentCourse', JSON.stringify(currentCourse));
      state.currentCourse = currentCourse;
    },
  },
  actions: {
    error({ commit }, errorMessage) {
      commit('error', errorMessage);
    },
    clearError({ commit }) {
      commit('clearError');
    },
    notification({ commit }, message) {
      commit('notification', message);
    },
    clearNotification({ commit }) {
      commit('clearNotification');
    },
    loading({ commit }) {
      commit('loading');
    },
    clearLoading({ commit }) {
      commit('clearLoading');
    },
    async fenixLogin({ commit }, code) {
      const authResponse = await RemoteServices.fenixLogin(code);
      commit('login', authResponse);
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async externalLogin({ commit }, user: ExternalUser) {
      const authResponse = await RemoteServices.externalLogin(
        user.username,
        user.password
      );
      commit('login', authResponse);
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoStudentLogin({ commit }) {
      const authResponse = await RemoteServices.demoStudentLogin(false);
      commit('login', authResponse);
      commit(
        'currentCourse',
        (Object.values(authResponse.user.courses)[0] as Course[])[0]
      );
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoNewStudentLogin({ commit }) {
      const authResponse = await RemoteServices.demoStudentLogin(true);
      commit('login', authResponse);
      commit(
        'currentCourse',
        (Object.values(authResponse.user.courses)[0] as Course[])[0]
      );
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoTeacherLogin({ commit }) {
      const authResponse = await RemoteServices.demoTeacherLogin();
      commit('login', authResponse);
      commit(
        'currentCourse',
        (Object.values(authResponse.user.courses)[0] as Course[])[0]
      );
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    async demoAdminLogin({ commit }) {
      const authResponse = await RemoteServices.demoAdminLogin();
      commit('login', authResponse);
      // localStorage.setItem("token", authResponse.token);
      // localStorage.setItem("userRole", authResponse.user.role);
    },
    logout({ commit }) {
      return new Promise<void>((resolve) => {
        commit('logout');
        // localStorage.removeItem("token");
        // localStorage.removeItem("userRole");
        resolve();
      });
    },
    currentCourse({ commit }, currentCourse) {
      commit('currentCourse', currentCourse);
    },
  },
  getters: {
    isLoggedIn(state): boolean {
      return !!state.token;
    },
    isAdmin(state): boolean {
      return (
        !!state.token &&
        state.user !== null &&
        (state.user.admin || state.user.role == 'DEMO_ADMIN')
      );
    },
    isTeacher(state): boolean {
      return (
        !!state.token && state.user !== null && state.user.role == 'TEACHER'
      );
    },
    isStudent(state): boolean {
      return (
        !!state.token && state.user !== null && state.user.role == 'STUDENT'
      );
    },
    getToken(state): string {
      return state.token;
    },
    getUser(state): AuthUser | null {
      return state.user;
    },
    getCurrentCourse(state): Course | null {
      return state.currentCourse;
    },
    getError(state): boolean {
      return state.error;
    },
    getErrorMessage(state): string {
      return state.errorMessage;
    },
    getNotification(state): boolean {
      return state.notification;
    },
    getNotificationMessageList(state): string[] {
      return state.notificationMessageList;
    },
    getLoading(state): boolean {
      return state.loading;
    },
  },
});
