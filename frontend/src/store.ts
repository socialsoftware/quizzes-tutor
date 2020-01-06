import Vue from "vue";
import Vuex from "vuex";
import RemoteServices from "@/services/RemoteServices";
import AuthDto from "@/models/auth/AuthDto";
import Course from "@/models/auth/Course";
import User from "@/models/auth/User";

interface State {
  token: string;
  user: User | null;
  currentCourse: Course | null;
  error: boolean;
  errorMessage: string;
  loading: boolean;
}

const state: State = {
  token: "",
  user: null,
  currentCourse: null,
  error: false,
  errorMessage: "",
  loading: false
};

Vue.use(Vuex);
Vue.config.devtools = true;

export default new Vuex.Store({
  state: state,
  mutations: {
    login(state, authResponse: AuthDto) {
      state.token = authResponse.token;
      state.user = authResponse.user;
    },
    logout(state) {
      state.token = "";
      state.user = null;
      state.currentCourse = null;
    },
    error(state, errorMessage: string) {
      state.error = true;
      state.errorMessage = errorMessage;
    },
    clearError(state) {
      state.error = false;
      state.errorMessage = "";
    },
    loading(state) {
      state.loading = true;
    },
    clearLoading(state) {
      state.loading = false;
    },
    currentCourse(state, currentCourse: Course) {
      state.currentCourse = currentCourse;
    }
  },
  actions: {
    error({ commit }, errorMessage) {
      commit("error", errorMessage);
    },
    clearError({ commit }) {
      commit("clearError");
    },
    loading({ commit }) {
      commit("loading");
    },
    clearLoading({ commit }) {
      commit("clearLoading");
    },
    async login({ commit }, code) {
      try {
        const authResponse = await RemoteServices.authenticate(code);
        commit("login", authResponse);
        localStorage.setItem("token", authResponse.token);
        localStorage.setItem("userRole", authResponse.user.role);
      } catch (error) {
        commit("logout");
        commit("error", error);
      }
    },
    logout({ commit }) {
      return new Promise(resolve => {
        commit("logout");
        localStorage.removeItem("token");
        localStorage.removeItem("userRole");
        resolve();
      });
    },
    currentCourse({ commit }, currentCourse) {
      commit("currentCourse", currentCourse);
    }
  },
  getters: {
    isLoggedIn(state): boolean {
      return !!state.token;
    },
    isAdmin(state): boolean {
      return !!state.token && state.user !== null && state.user.role == "ADMIN";
    },
    isTeacher(state): boolean {
      return (
        !!state.token &&
        state.user !== null &&
        (state.user.role == "TEACHER" || state.user.role == "ADMIN")
      );
    },
    isStudent(state): boolean {
      return (
        !!state.token &&
        state.user !== null &&
        (state.user.role == "STUDENT" || state.user.role == "ADMIN")
      );
    },
    getToken(state): string {
      return state.token;
    },
    getUser(state): User | null {
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
    getLoading(state): boolean {
      return state.loading;
    }
  }
});
