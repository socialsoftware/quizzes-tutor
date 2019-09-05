<template>
  <v-app id="inspire">
    <v-toolbar color="primary">
      <v-toolbar-title class="white-text">
        <v-btn href="/" flat dark>
          Software Architecture Quizzes
        </v-btn></v-toolbar-title
      >

      <v-spacer></v-spacer>

      <v-toolbar-items>
        <v-btn v-if="isAdmin" to="/admin-management" flat dark disabled>
          Admin Management
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-menu offset-y v-if="isTeacher" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" flat dark>
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-tile to="/management/questions">
              <v-list-tile-action>
                <v-icon>question_answer</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Questions</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile to="/management/topics">
              <v-list-tile-action>
                <v-icon>category</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Topics</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile to="/management/quizzes">
              <v-list-tile-action>
                <v-icon>ballot</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Quizzes</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </v-list>
        </v-menu>

        <v-btn v-if="isTeacher" to="/studentStats" flat dark disabled>
          Students Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-menu offset-y v-if="isStudent" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" flat dark>
              Quizzes
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-tile to="/student/available" disabled>
              <v-list-tile-action>
                <v-icon>assignment</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Available</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile to="/student/create">
              <v-list-tile-action>
                <v-icon>create</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Create</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile to="/student/solved" disabled>
              <v-list-tile-action>
                <v-icon>done</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Solved</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </v-list>
        </v-menu>

        <v-btn v-if="isStudent" to="/stats" flat dark>
          Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn v-if="isStudent" to="/achievements" flat dark disabled>
          Achievements
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn v-if="isLoggedIn" @click="logout" flat dark>
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>

        <v-btn v-else :href="fenix_url" flat dark>
          Login <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
  </v-app>
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

  logout(): void {
    this.$store.dispatch("logout");
  }
}
</script>

<style scoped lang="scss">
.v-icon {
  padding-left: 10px;
}
</style>
