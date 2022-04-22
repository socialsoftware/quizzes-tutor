<template>
  <nav>
    <v-app-bar clipped-left color="primary">
      <v-app-bar-nav-icon
        aria-label="Menu"
        class="hidden-md-and-up"
        @click.stop="drawer = !drawer"
      />

      <v-toolbar-title data-cy="homeLink">
        <v-btn
          v-if="currentCourse"
          active-class="no-active"
          dark
          text
          tile
          to="/"
        >
          {{ currentCourse.name }}
        </v-btn>
        <v-btn v-else active-class="no-active" dark text tile to="/">
          {{ appName }}
        </v-btn>
      </v-toolbar-title>

      <v-spacer />

      <v-toolbar-items class="hidden-sm-and-down" hide-details>
        <v-menu v-if="isTeacher && currentCourse" offset-y open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn dark data-cy="managementMenuButton" text v-on="on">
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item
              data-cy="questionsTeacherMenuButton"
              to="/management/questions"
            >
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Questions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item
              data-cy="manageTopicsMenuButton"
              to="/management/topics"
            >
              <v-list-item-action>
                <v-icon>category</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Topics</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item
              data-cy="quizzesTeacherMenuButton"
              to="/management/quizzes"
            >
              <v-list-item-action>
                <v-icon>ballot</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Quizzes</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/assessments">
              <v-list-item-action>
                <v-icon>book</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Assessments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/students">
              <v-list-item-action>
                <v-icon>school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Students</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item
              data-cy="discussionsTeacherButton"
              to="/management/discussions"
            >
              <v-list-item-action>
                <v-icon>fas fa-comment-dots</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Discussions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item
              data-cy="submissionTeacherMenuButton"
              to="/management/submissions"
            >
              <v-list-item-action>
                <v-icon>fas fa-user-edit</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Submissions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/tournaments">
              <v-list-item-action>
                <v-icon>fas fa-trophy</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Tournaments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/export">
              <v-list-item-action>
                <v-icon>fas fa-download</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Export</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu v-if="isStudent && currentCourse" offset-y open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn dark data-cy="quizzesStudentMenuButton" text v-on="on">
              Quizzes
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/available">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Available</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/create">
              <v-list-item-action>
                <v-icon>create</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Create</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/scan">
              <v-list-item-action>
                <v-icon>fas fa-qrcode</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Scan</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/code">
              <v-list-item-action>
                <v-icon>fas fa-terminal</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Code</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/solved">
              <v-list-item-action>
                <v-icon>done</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Solved</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item
              data-cy="discussionsStudentMenuButton"
              to="/student/discussions"
            >
              <v-list-item-action>
                <v-icon>fas fa-comment-dots</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Discussions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu v-if="isStudent && currentCourse" offset-y open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn dark data-cy="Tournament" text v-on="on">
              Tournaments
              <v-icon>fas fa-trophy</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item data-cy="Open" to="/student/tournaments/open">
              <v-list-item-action>
                <v-icon>fas fa-medal</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Open Tournaments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item data-cy="Closed" to="/student/tournaments/closed">
              <v-list-item-action>
                <v-icon>fas fa-award</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Closed Tournaments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn
          v-if="isStudent && currentCourse"
          dark
          data-cy="submissionStudentMenuButton"
          text
          to="/student/submissions"
        >
          Submissions
          <v-icon>fa-user-edit</v-icon>
        </v-btn>

        <v-btn
          v-if="isStudent && currentCourse"
          dark
          data-cy="dashboardMenuButton"
          text
          to="/student/dashboard"
        >
          Dashboard
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn
          v-if="isLoggedIn && moreThanOneCourse"
          active-class="no-active"
          dark
          text
          to="/courses"
        >
          Change course
          <v-icon>fa fa-book</v-icon>
        </v-btn>

        <v-menu v-if="isAdmin" offset-y open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn dark data-cy="administrationMenuButton" text v-on="on">
              Administration
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item data-cy="manageCoursesMenuButton" to="/admin/courses">
              <v-list-item-action>
                <v-icon>fas fa-school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Manage Courses</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/admin/export">
              <v-list-item-action>
                <v-icon>fas fa-download</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Export</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-toolbar-items>

      <v-toolbar-items class="hidden-sm-and-down" hide-details>
        <v-menu v-if="!isLoggedIn" offset-y open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn dark text v-on="on">
              Login
              <v-icon>fas fa-sign-in-alt</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item :href="fenixUrl">
              <v-list-item-content>
                <v-list-item-title>Fenix Login</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/login/external">
              <v-list-item-content>
                <v-list-item-title>External Login</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn
          v-if="isLoggedIn"
          dark
          data-cy="logoutButton"
          text
          @click="logout"
        >
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <!-- Start of mobile side menu -->
    <v-navigation-drawer v-model="drawer" absolute app dark temporary>
      <v-toolbar flat>
        <v-list>
          <v-list-item>
            <v-list-item-title class="title">Menu</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-toolbar>

      <v-list class="pt-0" dense>
        <!-- Management Group-->
        <v-list-group
          v-if="isTeacher && currentCourse"
          :value="false"
          prepend-icon="fas fa-file-alt"
        >
          <template v-slot:activator>
            <v-list-item-title>Management</v-list-item-title>
          </template>
          <v-list-item to="/management/questions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Questions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/topics">
            <v-list-item-action>
              <v-icon>category</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Topics</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/quizzes">
            <v-list-item-action>
              <v-icon>ballot</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Quizzes</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/assessments">
            <v-list-item-action>
              <v-icon>book</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Assessments</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/students">
            <v-list-item-action>
              <v-icon>school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Students</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/discussions">
            <v-list-item-action>
              <v-icon>fas fa-comment-dots</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Discussions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/submissions">
            <v-list-item-action>
              <v-icon>fas fa-user-edit</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Submissions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/tournaments">
            <v-list-item-action>
              <v-icon>fas fa-trophy</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Tournaments</v-list-item-title>
            </v-list-item-content>
          </v-list-item>

          <v-list-item to="/management/export">
            <v-list-item-action>
              <v-icon>fas fa-download</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Export</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Student Group-->
        <v-list-group
          v-if="isStudent && currentCourse"
          :value="false"
          prepend-icon="account_circle"
        >
          <template v-slot:activator>
            <v-list-item-title>Student</v-list-item-title>
          </template>

          <v-list-item
            v-if="isStudent && currentCourse"
            to="/student/available"
          >
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-title>Available Quizzes</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/create">
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-title>Create Quiz</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/scan">
            <v-list-item-action>
              <v-icon>fas fa-qrcode</v-icon>
            </v-list-item-action>
            <v-list-item-title>Scan</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/code">
            <v-list-item-action>
              <v-icon>fas fa-terminal</v-icon>
            </v-list-item-action>
            <v-list-item-content>Code</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/solved">
            <v-list-item-action>
              <v-icon>done</v-icon>
            </v-list-item-action>
            <v-list-item-title>Solved Quizzes</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/submissions">
            <v-list-item-action>
              <v-icon>fas fa-user-edit</v-icon>
            </v-list-item-action>
            <v-list-item-title>Submissions</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/dashboard">
            <v-list-item-action>
              <v-icon>fas fa-user</v-icon>
            </v-list-item-action>
            <v-list-item-title>Dashboard</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/discussions">
            <v-list-item-action>
              <v-icon>fas fa-comment-dots</v-icon>
            </v-list-item-action>
            <v-list-item-title>Discussions</v-list-item-title>
          </v-list-item>

          <v-list-item to="/student/all">
            <v-list-item-action>
              <v-icon>fas fa-calendar</v-icon>
            </v-list-item-action>
            <v-list-item-title>All Tournaments</v-list-item-title>
          </v-list-item>
          <v-list-item to="/student/tournaments/open">
            <v-list-item-action>
              <v-icon>fas fa-medal</v-icon>
            </v-list-item-action>
            <v-list-item-title>Open Tournaments</v-list-item-title>
          </v-list-item>
          <v-list-item to="/student/tournaments/closed">
            <v-list-item-action>
              <v-icon>fas fa-award</v-icon>
            </v-list-item-action>
            <v-list-item-title>Closed Tournaments</v-list-item-title>
          </v-list-item>
        </v-list-group>

        <!-- Administration Group-->
        <v-list-group
          v-if="isAdmin"
          :value="false"
          prepend-icon="fas fa-file-alt"
        >
          <template v-slot:activator>
            <v-list-item-title>Administration</v-list-item-title>
          </template>
          <v-list-item to="/admin/courses">
            <v-list-item-action>
              <v-icon>fas fa-school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Manage Courses</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/admin/export">
            <v-list-item-action>
              <v-icon>fas fa-download</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Export</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <v-list-item v-if="isLoggedIn && moreThanOneCourse" to="/courses">
          <v-list-item-action>
            <v-icon>fas fa-book</v-icon>
          </v-list-item-action>
          <v-list-item-content>Change course</v-list-item-content>
        </v-list-item>
        <v-list-item v-if="isLoggedIn" @click="logout">
          <v-list-item-action>
            <v-icon>fas fa-sign-out-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Logout</v-list-item-content>
        </v-list-item>
        <v-list-item v-else :href="fenixUrl">
          <v-list-item-action>
            <v-icon>fas fa-sign-in-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Login</v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <!-- End of mobile side menu -->
  </nav>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class TopBar extends Vue {
  appName: string = process.env.VUE_APP_NAME || 'ENV FILE MISSING';
  fenixUrl: string = process.env.VUE_APP_FENIX_URL || '';
  drawer: boolean = false;

  get currentCourse() {
    return this.$store.getters.getCurrentCourse;
  }

  get moreThanOneCourse() {
    return (
      this.$store.getters.getUser.coursesNumber > 1 &&
      this.$store.getters.getCurrentCourse
    );
  }

  get isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }

  get isTeacher() {
    return this.$store.getters.isTeacher;
  }

  get isAdmin() {
    return this.$store.getters.isAdmin;
  }

  get isStudent() {
    return this.$store.getters.isStudent;
  }

  async logout() {
    await this.$store.dispatch('logout');
    await this.$router.push({ name: 'home' }).catch(() => {});
  }
}
</script>

<style lang="scss" scoped>
.no-active::before {
  opacity: 0 !important;
}

nav {
  z-index: 300;
}
</style>
