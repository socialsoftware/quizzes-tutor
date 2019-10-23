<template>
  <nav>
    <v-app-bar color="primary" clipped-left>
      <v-app-bar-nav-icon
        @click.stop="drawer = !drawer"
        class="hidden-md-and-up"
      ></v-app-bar-nav-icon>
      <v-toolbar-title>
        <v-btn dark active-class="toolbar-title" text tile to="/">
          Software Architecture Quizzes
        </v-btn></v-toolbar-title
      >

      <v-spacer></v-spacer>

      <v-toolbar-items class="hidden-sm-and-down">
        <!--v-btn v-if="isAdmin" to="/admin-management" text dark disabled>
          Admin Management
          <v-icon>fas fa-user</v-icon>
        </v-btn-->

        <v-menu offset-y v-if="isTeacher" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
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
            <v-list-item to="/management/impexp">
              <v-list-item-action>
                <v-icon>cloud</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>ImpExp</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <!--v-btn v-if="isTeacher" to="/student/stats" text dark disabled>
          Students Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn-->

        <v-menu offset-y v-if="isStudent" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
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
            <v-list-item to="/student/solved">
              <v-list-item-action>
                <v-icon>done</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Solved</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn to="/student/stats" v-if="isStudent" text dark>
          Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn v-if="isStudent" to="/achievements" text dark disabled>
          Achievements
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn v-if="isLoggedIn" @click="logout" text dark>
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>

        <v-btn v-else :href="fenix_url" text dark>
          Login <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <!-- Start of mobile side menu -->
    <v-navigation-drawer app v-model="drawer" absolute dark temporary>
      <v-toolbar flat>
        <v-list>
          <v-list-item>
            <v-list-item-title class="title">Menu</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-toolbar>

      <v-list class="pt-0" dense>
        <v-list-item to="/student/available" v-if="isStudent" exact>
          <v-list-item-action>
            <v-icon>assignment</v-icon>
          </v-list-item-action>
          <v-list-item-content>Available Quizzes</v-list-item-content>
        </v-list-item>

        <v-list-item to="/student/create" v-if="isStudent">
          <v-list-item-action>
            <v-icon>create</v-icon>
          </v-list-item-action>
          <v-list-item-content>Create Quiz</v-list-item-content>
        </v-list-item>

        <v-list-item to="/student/solved" v-if="isStudent">
          <v-list-item-action>
            <v-icon>done</v-icon>
          </v-list-item-action>
          <v-list-item-content>Solved Quizzes</v-list-item-content>
        </v-list-item>

        <v-list-item to="/student/stats" v-if="isStudent">
          <v-list-item-action>
            <v-icon>fas fa-user</v-icon>
          </v-list-item-action>
          <v-list-item-content>Stats</v-list-item-content>
        </v-list-item>

        <v-list-item @click="logout" v-if="isLoggedIn">
          <v-list-item-action>
            <v-icon>fas fa-sign-out-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Logout</v-list-item-content>
        </v-list-item>
        <v-list-item :href="fenix_url" v-else>
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
import { Component, Vue } from "vue-property-decorator";

@Component
export default class TopBar extends Vue {
  fenix_url: string =
    "https://fenix.tecnico.ulisboa.pt/oauth/userdialog?client_id=" +
    process.env.VUE_APP_FENIX_CLIENT_ID +
    "&redirect_uri=" +
    process.env.VUE_APP_FENIX_REDIRECT_URI;
  drawer: boolean = false;

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
    await this.$store.dispatch("logout");
    await this.$router.push({ name: "home" }).catch(() => {});
  }
}
</script>

<style scoped lang="scss">
.v-icon {
  padding-left: 10px;
}

.toolbar-title::before {
  opacity: 0 !important;
}
</style>
