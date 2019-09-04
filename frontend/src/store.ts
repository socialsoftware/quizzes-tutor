import Vue from "vue";
import Vuex from "vuex";
import axios from "axios";
import User from "@/models/user/User";

interface TokenAndUser {
  token: string;
  user: User | undefined;
}

interface State {
  status: string;
  token: string;
  user: User | undefined;
  error: boolean;
  errorMessage: string;
}

const state: State = {
  status: "",
  token: localStorage.getItem("token") || "",
  user: undefined,
  error: false,
  errorMessage: ""
};

Vue.use(Vuex);
Vue.config.devtools = true;

export default new Vuex.Store({
  state: state,
  mutations: {
    auth_request(state) {
      state.status = "loading";
    },
    auth_success(state, tokenAndUser: TokenAndUser) {
      state.status = "success";
      state.token = tokenAndUser.token;
      state.user = tokenAndUser.user;
    },
    auth_error(state) {
      state.status = "error";
    },
    logout(state) {
      state.status = "";
      state.token = "";
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
    login({ commit }, code) {
      commit("auth_request");
      return new Promise((resolve, reject) => {
        axios
          .post(process.env.VUE_APP_ROOT_API + "/auth/fenix", { code: code })
          .then(response => {
            // handle success
            const token = response.data.token;
            const user = response.data.user;
            localStorage.setItem("token", token);
            commit("auth_success", { token: token, user: user });
            resolve(response);
          })
          .catch(err => {
            commit("auth_error");
            localStorage.removeItem("token");
            reject(err);
          });
      });
    },
    logout({ commit }) {
      return new Promise((resolve, reject) => {
        commit("logout");
        localStorage.removeItem("token");
        delete axios.defaults.headers.common["Authorization"];
        resolve();
      });
    }
  },
  getters: {
    getUser(state): User | undefined {
      return state.user;
    },
    isLoggedIn(state): boolean {
      return !!state.token;
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
