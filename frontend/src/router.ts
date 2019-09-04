import Vue from "vue";
import Router from "vue-router";
import Store from "./store";
import HomeView from "./views/HomeView.vue";
import LoginView from "./views/LoginView.vue";
import ManagementView from "@/views/management/ManagementView.vue";
import QuestionsView from "./views/management/QuestionsView.vue";
import TopicsView from "./views/management/TopicsView.vue";
import QuizzesView from "./views/management/QuizzesView.vue";
import StudentView from "@/views/student/StudentView.vue";
import QuizSetupView from "./views/student/QuizSetupView.vue";
import QuizView from "./views/student/QuizView.vue";
import ResultsView from "./views/student/ResultsView.vue";
import StatsView from "./views/student/StatsView.vue";
import NotFoundView from "./views/NotFoundView.vue";

Vue.use(Router);

let router = new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
      meta: { title: "Software Architecture" }
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { title: "Software Architecture - Login" }
    },
    {
      path: "/management",
      name: "management",
      component: ManagementView,
      children: [
        {
          path: "questions",
          name: "questions-management",
          component: QuestionsView,
          meta: {
            title: "Software Architecture - Questions",
            requiresAuth: true
          }
        },
        {
          path: "topics",
          name: "topics-management",
          component: TopicsView,
          meta: {
            title: "Software Architecture - Topics",
            requiresAuth: true
          }
        },
        {
          path: "quizzes",
          name: "quizzes-management",
          component: QuizzesView,
          meta: {
            title: "Software Architecture - Quizzes",
            requiresAuth: true
          }
        }
      ]
    },
    {
      path: "/student",
      name: "student",
      component: StudentView,
      children: [
        {
          path: "/setup",
          name: "setup",
          component: QuizSetupView,
          meta: {
            title: "Software Architecture - Quiz Setup",
            requiresAuth: true
          }
        },
        {
          path: "/quiz",
          name: "quiz",
          component: QuizView,
          meta: {
            title: "Software Architecture - Quiz",
            requiresAuth: true,
            requiresVerification: true
          }
        },
        {
          path: "/results",
          name: "results",
          component: ResultsView,
          meta: {
            title: "Software Architecture - Results",
            requiresAuth: true,
            requiresVerification: true
          }
        },
        {
          path: "/stats",
          name: "stats",
          component: StatsView,
          meta: {
            title: "Software Architecture - Stats",
            requiresAuth: true
          }
        }
      ]
    },
    {
      path: "**",
      name: "not-found",
      component: NotFoundView,
      meta: { title: "Page Not Found" }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (from.matched.some(record => record.meta.requiresVerification)) {
    if (confirm("are you sure?")) {
      next();
    } else {
      next(false);
    }
    return;
  }
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (Store.getters.isLoggedIn) {
      next();
      return;
    }
    next("/login");
  } else {
    next();
  }
});

router.afterEach((to, from) => {
  document.title = to.meta.title;
});

export default router;
