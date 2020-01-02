<template>
  <v-app id="app">
    <div class="img-container">
      <top-bar />
      <div class="scrollbar">
        <router-view />
        <loading />
      </div>
    </div>
    <error-message />
  </v-app>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "axios";
import TopBar from "@/components/TopBar.vue";
import ErrorMessage from "@/components/ErrorMessage.vue";
import Loading from "@/components/Loading.vue";
import "@/assets/css/_global.scss";
import "@/assets/css/_scrollbar.scss";
import "@/assets/css/_question.scss";
import "@/assets/css/bootstrap-social.css";
require("typeface-roboto");

@Component({
  components: { TopBar, ErrorMessage, Loading }
})
export default class App extends Vue {
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
}
</script>

<style scoped>
#app {
  text-align: center;
  color: #2c3e50;
  display: flex;
  flex-direction: column;
}

.img-container {
  display: flex;
  flex-direction: column;
  position: absolute;
  overflow: hidden;
  top: 0;
  margin: 0 !important;
  min-height: 100vh;
  width: 100vw;
  height: 100%;
  z-index: 1;
}
.img-container:after {
  content: " ";
  display: block;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background-image: url("assets/img/background_optimized.webp");
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: 0 0;
}

/*noinspection CssUnusedSymbol*/
.application--wrap {
  min-height: initial !important;
}
</style>
