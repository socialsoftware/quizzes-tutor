<template>
  <div id="app">
    <top-bar />
    <message-bar />
    <router-view />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "axios";
import TopBar from "@/views/components/TopBar.vue";
import MessageBar from "@/views/components/MessageBar.vue";
import "@/styles/_global.scss";
import "@/styles/_question.scss";

@Component({
  components: { TopBar, MessageBar }
})
export default class HomeView extends Vue {
  // noinspection JSUnusedGlobalSymbols
  created() {
    axios.interceptors.response.use(undefined, err => {
      return new Promise(() => {
        if (err.status === 401 && err.config && !err.config.__isRetryRequest) {
          this.$store.dispatch("logout");
        }
        throw err;
      });
    });
  }

  isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }

  logout() {
    this.$store.dispatch("logout").then(() => {
      this.$router.push("/login");
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

/*noinspection CssUnusedSymbol*/
.application--wrap {
  min-height: initial !important;
}
</style>
