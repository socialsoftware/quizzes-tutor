import Vue from "vue";
import Vuex from "vuex";
import RemoteServices from "@/services/RemoteServices";

interface State {
  token: string;
  userRole: string;
  error: boolean;
  errorMessage: string;
}

interface AuthResponse {
  token: string;
  userRole: string;
}

const state: State = {
  // TODO SWAP ME
  token: localStorage.getItem("token") || "",
  userRole: localStorage.getItem("userRole") || "",
  // token: "",
  // userRole: "",
  error: false,
  errorMessage: ""
};

Vue.use(Vuex);
Vue.config.devtools = true;

export default new Vuex.Store({
  state: state,
  mutations: {
    login(state, authResponse: AuthResponse) {
      state.token = authResponse.token;
      state.userRole = authResponse.userRole;
    },
    logout(state) {
      state.token = "";
      state.userRole = "";
    },
    error(state, errorMessage: string) {
      state.error = true;
      state.errorMessage = errorMessage;
    },
    clearError(state) {
      state.error = false;
      state.errorMessage = "";
    }
  },
  actions: {
    error({ commit }, errorMessage) {
      commit("error", errorMessage);
    },
    clearError({ commit }) {
      commit("clearError");
    },
    async login({ commit }, code) {
      try {
        const authResponse = await RemoteServices.authenticate(code);
        commit("login", authResponse);
        localStorage.setItem("token", authResponse.token);
        localStorage.setItem("userRole", authResponse.userRole);
      } catch (error) {
        commit("logout");
        commit("error", error);
      }
    },
    logout({ commit }) {
      return new Promise((resolve, reject) => {
        commit("logout");
        localStorage.removeItem("token");
        localStorage.removeItem("userRole");
        resolve();
      });
    }
  },
  getters: {
    isLoggedIn(state): boolean {
      return !!state.token;
    },
    isAdmin(state): boolean {
      return !!state.token && state.userRole == "ADMIN";
    },
    isTeacher(state): boolean {
      return (
        !!state.token &&
        (state.userRole == "TEACHER" || state.userRole == "ADMIN")
      );
    },
    isStudent(state): boolean {
      return (
        !!state.token &&
        (state.userRole == "STUDENT" || state.userRole == "ADMIN")
      );
    },
    getToken(state): string {
      return state.token;
    },
    getError(state): boolean {
      return state.error;
    },
    getErrorMessage(state): string {
      return state.errorMessage;
    }
  }
});
