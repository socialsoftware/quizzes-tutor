import Vue from "vue";
import Router from "vue-router";
import QuizView from "./views/QuizView.vue";
import HomeView from "./views/HomeView.vue";
import CreatorView from "./views/CreatorView.vue";
import LoginView from "./views/LoginView.vue";

Vue.use(Router);

export default new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/quiz",
      name: "quiz",
      component: QuizView
    },
    {
      path: "/creator",
      name: "creator",
      component: CreatorView
    },
    {
      path: "/login",
      name: "login",
      component: LoginView
    }
  ]
});
