<template>
  <v-app id="inspire">
    <v-toolbar dark color="primary">
      <v-toolbar-title class="white--text">
        <v-btn href="/" flat>
          Software Architecture Quizzes
        </v-btn></v-toolbar-title
      >

      <v-spacer></v-spacer>

      <v-toolbar-items>
        <v-menu offset-y v-if="isLoggedIn" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn
              color="primary"
              dark
              v-on="on"
            >
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-tile v-if="isLoggedIn" to="/management/questions">
              <v-list-tile-action>
                <v-icon>question_answer</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Questions</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile v-if="isLoggedIn" to="/management/topics">
              <v-list-tile-action>
                <v-icon>category</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Topics</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile v-if="isLoggedIn" to="/management/quizzes">
              <v-list-tile-action>
                <v-icon>ballot</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>Quizzes</v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </v-list>
        </v-menu>

        <v-btn v-if="isLoggedIn" to="/stats" flat>
          Stats
          <v-icon>fas fa-user</v-icon>
        </v-btn>

        <v-btn v-if="isLoggedIn" to="/" @click="logout" flat>
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>

        <v-btn v-else :href="fenix_url" flat>
          Login <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
  </v-app>
</template>

<script lang="ts">
import { Component, Vue} from "vue-property-decorator";
import Store from "@/store";

@Component
export default class TopBar extends Vue {
  fenix_url: string =
    "https://fenix.tecnico.ulisboa.pt/oauth/userdialog?client_id=" +
    process.env.VUE_APP_FENIX_CLIENT_ID +
    "&redirect_uri=" +
    process.env.VUE_APP_FENIX_REDIRECT_URI;

  get isLoggedIn() {
    return Store.state.token;
  }

  logout(): void {
    Store.dispatch("logout");
  }
}
</script>

<style scoped lang="scss">
.v-icon {
  padding-left: 10px;
}
</style>
