import Vue from 'vue';
import Router from 'vue-router';
import Store from './store';

import HomeView from './views/HomeView.vue';
import LoginView from './views/LoginView.vue';
import CourseSelectionView from './views/CourseSelectionView.vue';

import ManagementView from '@/views/teacher/ManagementView.vue';
import QuestionsView from './views/teacher/questions/QuestionsView.vue';
import TopicsView from './views/teacher/TopicsView.vue';
import QuizzesView from './views/teacher/quizzes/QuizzesView.vue';
import StudentsView from './views/teacher/students/StudentsView.vue';
import StudentView from '@/views/student/StudentView.vue';
import AvailableQuizzesView from './views/student/AvailableQuizzesView.vue';
import SolvedQuizzesView from './views/student/SolvedQuizzesView.vue';
import QuizView from './views/student/QuizView.vue';
import ResultsView from './views/student/ResultsView.vue';
import StatsView from './views/student/StatsView.vue';

import AdminManagementView from './views/admin/AdminManagementView.vue';
import NotFoundView from './views/NotFoundView.vue';
import ImpExpView from '@/views/teacher/impexp/ImpExpView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';

Vue.use(Router);

let router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: 'Software Architecture', requiredAuth: 'None' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { title: 'Software Architecture - Login', requiredAuth: 'None' }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: 'Software Architecture - Course Selection',
        requiredAuth: 'None'
      }
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView,
      children: [
        {
          path: 'questions',
          name: 'questions-management',
          component: QuestionsView,
          meta: {
            title: 'Software Architecture - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: 'Software Architecture - Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: 'Software Architecture - Quizzes',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: 'Software Architecture - Assessment Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'students',
          name: 'students-management',
          component: StudentsView,
          meta: {
            title: 'Software Architecture - Students',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'impexp',
          name: 'impexp-management',
          component: ImpExpView,
          meta: {
            title: 'Software Architecture - ImpExp',
            requiredAuth: 'Teacher'
          }
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: StudentView,
      children: [
        {
          path: 'available',
          name: 'available-quizzes',
          component: AvailableQuizzesView,
          meta: {
            title: 'Software Architecture - Available Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: 'Software Architecture - Create Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: 'Software Architecture - Solved Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: 'Software Architecture - Quiz',
            requiredAuth: 'Student',
            requiresVerification: true
          }
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: 'Software Architecture - Results',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsView,
          meta: {
            title: 'Software Architecture - Stats',
            requiredAuth: 'Student'
          }
        } /*,
        {
          path: "achievements",
          name: "achievements",
          component: AchievementsView,
          meta: {
            title: "Software Architecture - Achievements",
            requiredAuth: "Student"
          }
        }*/
      ]
    },
    {
      path: '/admin-management',
      name: 'admin-management',
      component: AdminManagementView,
      meta: {
        title: 'Software Architecture - AdminManagement',
        requiredAuth: 'Admin'
      }
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (from.matched.some(record => record.meta.requiresVerification)) {
    let a = confirm('Are you sure?');
    if (!a) {
      next(false);
      return;
    }
  }

  if (to.meta.requiredAuth == 'None') {
    next();
  } else if (to.meta.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta.requiredAuth == 'Teacher' && Store.getters.isTeacher) {
    next();
  } else if (to.meta.requiredAuth == 'Student' && Store.getters.isStudent) {
    next();
  } else {
    next('/');
  }

  // await Store.dispatch("clearError");
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
