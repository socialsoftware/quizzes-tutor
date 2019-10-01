<template>
  <div class="home-container">
    <h1 id="home-title" class="display-2 font-weight-thin mb-3">
      Software Architecture
    </h1>
    <v-btn v-if="!isLoggedIn" :href="fenix_url" depressed color="primary"
      >Log in</v-btn
    >

    <div class="bottom-row-container">
      <img
        :src="require('../assets/ist.png')"
        class="logo"
        alt="Técnico Logo"
      />
      <div>
        <a
          v-if="isLoggedIn"
          class="btn btn-block btn-social btn-github"
          href="https://github.com/socialsoftware/as-tutor"
          target="_blank"
        >
          <span class="fab fa-github"></span> View code
        </a>
      </div>
      <div>
        <a
          v-if="isLoggedIn"
          class="btn btn-block btn-social btn-github"
          href="https://github.com/socialsoftware/as-tutor/issues"
          target="_blank"
        >
          <span class="fab fa-github"></span> Bug report
        </a>
      </div>
      <img
        :src="require('../assets/impress.png')"
        class="logo"
        alt="IMPRESS Logo"
      />
    </div>
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

<style lang="scss">
.home-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  flex-wrap: nowrap;
  justify-content: center;
  align-items: center;

  #home-title {
    box-sizing: border-box;
    color: rgb(255, 255, 255);
    min-height: auto;
    min-width: auto;
    text-align: center;
    text-decoration: none solid rgb(255, 255, 255);
    text-rendering: optimizelegibility;
    text-size-adjust: 100%;
    column-rule-color: rgb(255, 255, 255);
    perspective-origin: 229.922px 34px;
    transform-origin: 229.922px 34px;
    caret-color: rgb(255, 255, 255);
    background: rgba(0, 0, 0, 0.75) none no-repeat scroll 0 0 / auto padding-box
      border-box;
    border: 0 none rgb(255, 255, 255);
    font: normal normal 100 normal 45px / 48px Roboto, sans-serif !important;
    margin-bottom: 100px !important;
    outline: rgb(255, 255, 255) none 0;
    padding: 10px 20px;
  }

  .bottom-row-container {
    display: flex; /* or inline-flex */
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    max-height: 100px;
    position: absolute;
    bottom: 0;
    overflow: hidden;

    .logo {
      flex-shrink: 1;
      width: 20%;
      max-width: 200px;
      padding: 2%;
    }

    div {
      flex-grow: 2;

      .btn-github {
        color: white !important;
        width: 125px;
        margin: 0 auto;
        font: normal normal 400 normal 14px / 21px Roboto, sans-serif;
      }
    }
  }
}
</style>
