import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import LoginView from '@/views/user/LoginView.vue';
import ExternalLoginView from '@/views/user/ExternalLoginView.vue';
import RegistrationConfirmationView from '@/views/user/RegistrationConfirmationView.vue';
import CourseSelectionView from '@/views/CourseSelectionView.vue';

import HomeView from '@/views/HomeView.vue';
import ManagementView from '@/views/teacher/ManagementView.vue';
import QuestionsView from '@/views/teacher/questions/QuestionsView.vue';
import TopicsView from '@/views/teacher/TopicsView.vue';
import TournamentsView from '@/views/teacher/tournaments/TournamentsView.vue';
import QuizzesView from '@/views/teacher/quizzes/QuizzesView.vue';
import StudentsView from '@/views/teacher/students/StudentsView.vue';
import StudentView from '@/views/student/StudentView.vue';
import AvailableQuizzesView from '@/views/student/AvailableQuizzesView.vue';
import SolvedQuizzesView from '@/views/student/SolvedQuizzesView.vue';
import QuizView from '@/views/student/quiz/QuizView.vue';
import ResultsView from '@/views/student/quiz/ResultsView.vue';
import StatsView from '@/views/student/StatsView.vue';
import ScanView from '@/views/student/ScanView.vue';
import CodeView from '@/views/student/CodeView.vue';

import TournamentsListView from './views/student/tournament/TournamentsListView.vue';

import AdminManagementView from '@/views/admin/AdminManagementView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import ExportCourseView from '@/views/teacher/impexp/ExportCourseView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';

import QuestionSubmissionView from '@/views/questionsubmission/QuestionSubmissionView.vue';
import SortQuestionSubmissionsByStudentView from '@/views/questionsubmission/SortQuestionSubmissionsByStudentView.vue';
import StudentDiscussionsView from '@/views/student/discussions/StudentDiscussionsView.vue';
import TeacherDiscussionsView from '@/views/teacher/discussions/TeacherDiscussionsView.vue';
import TournamentResultsView from '@/views/student/tournament/TournamentResultsView.vue';
import ExportAllView from '@/views/admin/ExportAllView.vue';
import CoursesView from '@/views/admin/courses/CoursesView.vue';

Vue.use(Router);

const APP_NAME = process.env.VUE_APP_NAME || '';

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: APP_NAME, requiredAuth: 'None' },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Login Fenix',
      },
    },
    {
      path: '/login/external',
      name: 'external-login',
      component: ExternalLoginView,
      meta: {
        requiredAuth: 'None',
        title: APP_NAME + ' - Login External',
      },
    },
    {
      path: '/registration/confirmation',
      name: 'registration-confirmation',
      component: RegistrationConfirmationView,
      meta: {
        title: APP_NAME + ' - Registration Confirmation',
        requiredAuth: 'None',
      },
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: APP_NAME + ' - Course Selection',
        requiredAuth: 'None',
      },
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
            title: APP_NAME + ' - Questions',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: APP_NAME + ' - Topics',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'tournaments',
          name: 'tournaments-management',
          component: TournamentsView,
          meta: {
            title: APP_NAME + ' - Tournaments',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: APP_NAME + ' - Quizzes',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: APP_NAME + ' - Assessment Topics',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'students',
          name: 'students-management',
          component: StudentsView,
          meta: {
            title: APP_NAME + ' - Students',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'export',
          name: 'export-management',
          component: ExportCourseView,
          meta: {
            title: APP_NAME + ' - Export',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'submissions/students',
          name: 'sort-by-student-submissions',
          component: SortQuestionSubmissionsByStudentView,
          meta: {
            title: APP_NAME + ' - Sort by Student Submissions',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'submissions',
          name: 'teacher-submissions',
          component: QuestionSubmissionView,
          meta: {
            title: APP_NAME + ' - Submissions',
            requiredAuth: 'Teacher',
          },
        },
        {
          path: 'discussions',
          name: 'teacher-discussions',
          component: TeacherDiscussionsView,
          meta: {
            title: APP_NAME + ' - Discussions',
            requiredAuth: 'Teacher',
          },
        },
      ],
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
            title: APP_NAME + ' - Available Quizzes',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: APP_NAME + ' - Create Quizzes',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: APP_NAME + ' - Solved Quizzes',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: APP_NAME + ' - Quiz',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: APP_NAME + ' - Results',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsView,
          meta: {
            title: APP_NAME + ' - Stats',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'scan',
          name: 'scan',
          component: ScanView,
          meta: {
            title: APP_NAME + ' - Scan',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'discussions',
          name: 'discussions',
          component: StudentDiscussionsView,
          meta: {
            title: APP_NAME + ' - Discussion',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'code',
          name: 'code',
          component: CodeView,
          meta: {
            title: APP_NAME + ' - Code',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'tournaments/open',
          name: 'open-tournaments',
          component: TournamentsListView,
          props: { type: 'OPEN' },
          meta: {
            title: APP_NAME + ' - Tournament',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'tournaments/closed',
          name: 'closed-tournaments',
          component: TournamentsListView,
          props: { type: 'CLOSED' },
          meta: {
            title: APP_NAME + ' - Tournament',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'tournament',
          name: 'tournament-participants',
          component: TournamentResultsView,
          props: (route) => ({ id: route.query.id }),
          meta: {
            title: APP_NAME + ' - Tournament Participants',
            requiredAuth: 'Student',
          },
        },
        {
          path: 'submissions',
          name: 'student-submissions',
          component: QuestionSubmissionView,
          meta: {
            title: APP_NAME + ' - Submissions',
            requiredAuth: 'Student',
          },
        },
      ],
    },
    {
      path: '/teacher/tournament',
      name: 'tournament-dashboard',
      component: TournamentResultsView,
      props: (route) => ({ id: route.query.id }),
      meta: {
        title: APP_NAME + ' - Tournament Dashboard',
        requiredAuth: 'Teacher',
      },
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminManagementView,
      children: [
        {
          path: 'courses',
          name: 'courseAdmin',
          component: CoursesView,
          meta: {
            title: APP_NAME + ' - Manage courses',
            requiredAuth: 'Admin',
          },
        },
        {
          path: 'export',
          name: 'exportAll',
          component: ExportAllView,
          meta: {
            title: APP_NAME + ' - Manage courses',
            requiredAuth: 'Admin',
          },
        },
      ],
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' },
    },
  ],
});

router.beforeEach(async (to, from, next) => {
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
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
