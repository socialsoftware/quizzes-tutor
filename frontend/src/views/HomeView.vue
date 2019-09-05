<template>
  <div class="img-container">
    <v-parallax height="900" dark>
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
.img-container {
  position: absolute;
  top: 0;
  z-index: -1;
  height: 100vh;
  background-image: url("../assets/01.jpg");
  background-repeat: no-repeat;
  background-size: cover;
  margin: 0 !important;
  width: 100vw;
}
.v-parallax__image {
  transform: translate(-50%) !important;
  z-index: 1;
}
#home-title {
  font-family: "Graduate", cursive;
  background-color: rgba(0, 0, 0, 0.4);
  padding: 10px 20px;
  font-weight: bold;
}
</style>
