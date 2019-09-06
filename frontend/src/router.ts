import Vue from "vue";
import Router from "vue-router";
import Store from "./store";

import HomeView from "./views/HomeView.vue";
import LoginView from "./views/LoginView.vue";

import ManagementView from "@/views/management/ManagementView.vue";
import QuestionsView from "./views/management/QuestionsView.vue";
import TopicsView from "./views/management/TopicsView.vue";
import StudentStatsView from "./views/StudentStatsView.vue";
import QuizzesView from "./views/management/quizzes/QuizzesView.vue";
import StudentView from "@/views/student/StudentView.vue";
import AvailableQuizzesView from "./views/student/AvailableQuizzesView.vue";
import CreateQuizzesView from "./views/student/CreateQuizzesView.vue";
import SolvedQuizzesView from "./views/student/SolvedQuizzesView.vue";
import QuizView from "./views/student/QuizView.vue";
import ResultsView from "./views/student/ResultsView.vue";
import StatsView from "./views/student/StatsView.vue";
import AchievementsView from "./views/student/AchievementsView.vue";

import AdminManagementView from "./views/AdminManagementView.vue";
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
      meta: { title: "Software Architecture", requiredAuth: true }
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { title: "Software Architecture - Login", requiredAuth: true }
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
            requiredAuth: Store.getters.isTeacher
          }
        },
        {
          path: "topics",
          name: "topics-management",
          component: TopicsView,
          meta: {
            title: "Software Architecture - Topics",
            requiredAuth: Store.getters.isTeacher
          }
        },
        {
          path: "quizzes",
          name: "quizzes-management",
          component: QuizzesView,
          meta: {
            title: "Software Architecture - Quizzes",
            requiredAuth: Store.getters.isTeacher
          }
        }
      ]
    },
    {
      path: "/students-stats",
      name: "students-stats",
      component: StudentStatsView,
      meta: {
        title: "Software Architecture - StudentsStats",
        requiredAuth: Store.getters.isTeacher
      }
    },
    {
      path: "/student",
      name: "student",
      component: StudentView,
      children: [
        {
          path: "available",
          name: "available-quizzes",
          component: AvailableQuizzesView,
          meta: {
            title: "Software Architecture - Available Quizzes",
            requiredAuth: Store.getters.isStudent
          }
        },
        {
          path: "create",
          name: "create-quizzes",
          component: CreateQuizzesView,
          meta: {
            title: "Software Architecture - Create Quizzes",
            requiredAuth: Store.getters.isStudent
          }
        },
        {
          path: "solved",
          name: "solved-quizzes",
          component: SolvedQuizzesView,
          meta: {
            title: "Software Architecture - Solved Quizzes",
            requiredAuth: Store.getters.isStudent
          }
        },
        {
          path: "quiz",
          name: "quiz",
          component: QuizView,
          meta: {
            title: "Software Architecture - Quiz",
            requiredAuth: Store.getters.isStudent,
            requiresVerification: true
          }
        },
        {
          path: "results",
          name: "results",
          component: ResultsView,
          meta: {
            title: "Software Architecture - Results",
            requiredAuth: Store.getters.isStudent,
            requiresVerification: true
          }
        },
        {
          path: "stats",
          name: "stats",
          component: StatsView,
          meta: {
            title: "Software Architecture - Stats",
            requiredAuth: Store.getters.isStudent
          }
        },
        {
          path: "achievements",
          name: "achievements",
          component: AchievementsView,
          meta: {
            title: "Software Architecture - Achievements",
            requiredAuth: Store.getters.isStudent
          }
        }
      ]
    },
    {
      path: "/admin-management",
      name: "admin-management",
      component: AdminManagementView,
      meta: {
        title: "Software Architecture - AdminManagement",
        requiredAuth: Store.getters.isAdmin
      }
    },
    {
      path: "**",
      name: "not-found",
      component: NotFoundView,
      meta: { title: "Page Not Found", requiredAuth: true }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  /*TODO
  if (from.matched.some(record => record.meta.requiresVerification)) {
    if (confirm("are you sure?")) {
      next();
    } else {
      next(false);
    }
    return;
  }*/
  if (to.matched.some(record => record.meta.requiredAuth)) {
    next();
    return;
  } else {
    next("/");
  }
});

router.afterEach((to, from) => {
  document.title = to.meta.title;
});

export default router;
