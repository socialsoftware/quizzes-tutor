import Vue from "vue";
import Vuex from "vuex";
import axios from "axios";
import { TokenAndUser } from "@/types";

Vue.use(Vuex);
Vue.config.devtools = true;

export default new Vuex.Store({
  state: {
    status: "",
    token: localStorage.getItem("token") || "",
    user: ""
  },
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
    }
  },
  actions: {
    login({ commit }, code) {
      commit("auth_request");
      console.log("store code", code);
      return new Promise((resolve, reject) => {
        axios
          .get(process.env.VUE_APP_ROOT_API + "/auth/fenix", {
            params: {
              code: code
            }
          })
          .then(response => {
            // handle success
            console.log(response);
            const token = response.data.token;
            const user = response.data.user;
            localStorage.setItem("token", token);
            // Add the following line:
            axios.defaults.headers.common["Authorization"] = token;
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
    getName(state): string {
      const user: string = state.user;
      return user || "";
    },
    isLoggedIn(state): boolean {
      return !!state.token;
    },
    getToken(state): string {
      return state.token;
    }
  }
});
