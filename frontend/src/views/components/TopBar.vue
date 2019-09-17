<template>
  <v-toolbar primary dark>
    <v-toolbar-title class="white-">
      <v-btn text href="/">
        Software Architecture Quizzes
      </v-btn></v-toolbar-title
    >

    <v-spacer></v-spacer>

    <v-toolbar-items>
      <!--v-btn v-if="isAdmin" to="/admin-management"   disabled>
          Admin Management
          <v-icon>fas fa-user</v-icon>
        </v-btn-->

      <v-menu offset-y v-if="isTeacher" open-on-hover>
        <template v-slot:activator="{ on }">
          <v-btn text v-on="on">
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
        </v-list>
      </v-menu>

      <!--v-btn v-if="isTeacher" to="/student/stats"   disabled>
          Students Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn-->

      <v-menu offset-y v-if="isStudent" open-on-hover>
        <template v-slot:activator="{ on }">
          <v-btn text v-on="on">
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
          <v-list-item to="/student/create" disabled>
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

      <v-btn text to="/student/stats" v-if="isStudent">
        Stats
        <v-icon>fas fa-user</v-icon>
      </v-btn>

      <v-btn text v-if="isStudent" to="/achievements" disabled>
        Achievements
        <v-icon>fas fa-user</v-icon>
      </v-btn>

      <v-btn text v-if="isLoggedIn" @click="logout">
        Logout
        <v-icon>fas fa-sign-out-alt</v-icon>
      </v-btn>

      <v-btn text v-else :href="fenix_url">
        Login <v-icon>fas fa-sign-in-alt</v-icon>
      </v-btn>
    </v-toolbar-items>
  </v-toolbar>
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

  async logout() {
    await this.$store.dispatch("logout");
    await this.$store.dispatch("clearError");
    await this.$router.push({ name: "home" }).catch(() => {});
  }
}
</script>

<style scoped lang="scss">
.v-icon {
  padding-left: 10px;
}
</style>
