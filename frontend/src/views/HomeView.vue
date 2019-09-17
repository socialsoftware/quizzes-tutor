<template>
  <div class="home-container">
    <h1 id="home-title" class="display-2 font-weight-thin mb-3">
      Software Architecture
    </h1>
    <v-btn v-if="!isLoggedIn" :href="fenix_url" depressed color="primary"
      >Log in</v-btn
    >
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
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn-github {
  color: white !important;
  width: 125px;
  margin: auto;
}

#home-title {
  box-sizing: border-box;
  color: rgb(255, 255, 255);
  height: 68px;
  min-height: auto;
  min-width: auto;
  text-align: center;
  text-decoration: none solid rgb(255, 255, 255);
  text-rendering: optimizelegibility;
  text-size-adjust: 100%;
  width: 459.844px;
  column-rule-color: rgb(255, 255, 255);
  perspective-origin: 229.922px 34px;
  transform-origin: 229.922px 34px;
  caret-color: rgb(255, 255, 255);
  background: rgba(0, 0, 0, 0.75) none no-repeat scroll 0% 0% / auto padding-box
    border-box;
  border: 0px none rgb(255, 255, 255);
  font: normal normal 100 normal 45px / 48px Roboto, sans-serif;
  margin: 0px auto;
  outline: rgb(255, 255, 255) none 0px;
  padding: 10px 20px;
}
</style>
