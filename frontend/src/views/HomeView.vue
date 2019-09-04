<template>
  <div>
    <v-parallax dark :src="require('../assets/01.jpg')">
      <v-layout align-center column justify-center>
        <h1 id="home-title" class="display-2 font-weight-thin mb-3">
          Software Architecture
        </h1>
        <h4 class="subheading">Tutor</h4>

        <div>
          <v-btn v-if="isLoggedIn" to="/setup" depressed color="primary"
            >Answer quiz</v-btn
          >
          <v-btn v-else :href="fenix_url" depressed color="primary"
            >Log in</v-btn
          >
        </div>
      </v-layout>
    </v-parallax>

    <v-layout align-center>
      <v-item-group v-model="window" class="shrink mr-4" mandatory tag="v-flex">
        <v-item v-for="n in length" :key="n">
          <div slot-scope="{ active, toggle }">
            <v-btn color="blue" :input-value="active" icon @click="toggle">
              <v-icon color="pink">mdi-record</v-icon>
            </v-btn>
          </div>
        </v-item>
      </v-item-group>

      <v-flex>
        <v-window v-model="window" class="elevation-1" vertical>
          <v-window-item v-for="n in length" :key="n">
            <v-card flat>
              <v-card-text>
                <v-layout align-center mb-3>
                  <v-avatar color="grey" class="mr-3"></v-avatar>
                  <strong class="title">Title {{ n }}</strong>
                  <v-spacer></v-spacer>
                  <v-btn icon>
                    <v-icon>mdi-account</v-icon>
                  </v-btn>
                </v-layout>

                <p>
                  848484848484
                </p>

                <p>
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
                  do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                  Ut enim ad minim veniam, quis nostrud exercitation ullamco
                  laboris nisi ut aliquip ex ea commodo consequat. Duis aute
                  irure dolor in reprehenderit in voluptate velit esse cillum
                  dolore eu fugiat nulla pariatur. Excepteur sint occaecat
                  cupidatat non proident, sunt in culpa qui officia deserunt
                  mollit anim id est laborum.
                </p>

                <p>
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed
                  do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                  Ut enim ad minim veniam, quis nostrud exercitation ullamco
                  laboris nisi ut aliquip ex ea commodo consequat. Duis aute
                  irure dolor in reprehenderit in voluptate velit esse cillum
                  dolore eu fugiat nulla pariatur. Excepteur sint occaecat
                  cupidatat non proident, sunt in culpa qui officia deserunt
                  mollit anim id est laborum.
                </p>
              </v-card-text>
            </v-card>
          </v-window-item>
        </v-window>
      </v-flex>
    </v-layout>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Store from "@/store";

@Component
export default class HomeView extends Vue {
  window: number = 0;
  length: number = 3;
  fenix_url: string =
    "https://fenix.tecnico.ulisboa.pt/oauth/userdialog?client_id=" +
    process.env.VUE_APP_FENIX_CLIENT_ID +
    "&redirect_uri=" +
    process.env.VUE_APP_FENIX_REDIRECT_URI;

  get isLoggedIn() {
    return Store.state.token;
  }
}
</script>

<style>
.v-parallax__image {
  transform: translate(-50%) !important;
}
#home-title {
  font-family: "Graduate", cursive;
  background-color: rgba(0, 0, 0, 0.4);
  padding: 10px 20px;
  font-weight: bold;
}
</style>
