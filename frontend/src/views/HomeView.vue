<template>
  <div>
    <v-parallax height="800" dark>
      <v-layout align-center column justify-center>
        <h1 id="home-title" class="display-2 font-weight-thin mb-3">
          Software Architecture
        </h1>

        <div>
          <v-btn v-if="!isLoggedIn" :href="fenix_url" depressed color="primary"
            >Log in</v-btn
          >
        </div>
      </v-layout>
    </v-parallax>
    <a
      v-if="isLoggedIn"
      class="btn btn-block btn-social btn-github"
      href="https://github.com/socialsoftware/as-tutor"
      target="_blank"
    >
      <span class="fab fa-github"></span> View code
    </a>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Store from "@/store";
import "@/styles/bootstrap-social.css";

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
.btn-github {
  color: white !important;
  width: 125px;
  margin: auto;
}

.v-parallax__image {
  transform: translate(-50%) !important;
  z-index: 10;
}
#home-title {
  font-family: "Graduate", cursive;
  background-color: rgba(0, 0, 0, 0.75);
  padding: 10px 20px;
  font-weight: bold;
}
</style>
