import Vue from "vue";
import Router from "vue-router";
import store from "./store";
import HomeView from "./views/HomeView.vue";
import LoginView from "./views/LoginView.vue";
import SetupView from "./views/SetupView.vue";
import QuizView from "./views/QuizView.vue";
import StatsView from "./views/StatsView.vue";
import NotFoundView from "./views/NotFoundView.vue";

Vue.use(Router);

let router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/login",
      name: "login",
      component: LoginView
    },
    {
      path: "/setup",
      name: "setup",
      component: SetupView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: "/quiz",
      name: "quiz",
      component: QuizView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: "/stats",
      name: "stats",
      component: StatsView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: "**",
      name: "not-found",
      component: NotFoundView
    }
  ]
});

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (store.getters.isLoggedIn) {
      next();
      return;
    }
    next("/login");
  } else {
    next();
  }
});

export default router;
