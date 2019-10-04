<template>
  <v-app id="app">
    <div class="img-container">
      <top-bar />
      <div class="scrollbar">
        <router-view />
      </div>
    </div>
    <message-bar />
  </v-app>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "axios";
import TopBar from "@/views/components/TopBar.vue";
import MessageBar from "@/views/components/MessageBar.vue";
import "@/styles/_global.scss";
import "@/styles/_scrollbar.scss";
import "@/styles/_question.scss";

@Component({
  components: { TopBar, MessageBar }
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
  background-image: url("./assets/background.jpg");
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: 0 0;
}

/*noinspection CssUnusedSymbol*/
.application--wrap {
  min-height: initial !important;
}
</style>
