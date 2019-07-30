<template>
  <div id="app">
    <top-bar />
    <router-view />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "axios";
import TopBar from "@/views/components/TopBar.vue";
import "@/styles/_global.scss";
import "@/styles/_question.scss";

@Component({
  components: { TopBar }
})
export default class HomeView extends Vue {
  isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }
  logout() {
    this.$store.dispatch("logout").then(() => {
      this.$router.push("/login");
    });
  }

  created() {
    axios.interceptors.response.use(undefined, err => {
      return new Promise((resolve, reject) => {
        if (err.status === 401 && err.config && !err.config.__isRetryRequest) {
          this.$store.dispatch("logout");
        }
        throw err;
      });
    });
  }
}
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100vh;
}
.application--wrap {
  min-height: initial !important;
}
</style>
