<template>
  <v-app id="app">
    <div class="img-container">
      <top-bar />
      <message-bar />
      <div class="scrollbar">
        <router-view />
      </div>
    </div>
  </v-app>
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
export default class App extends Vue {
  // noinspection JSUnusedGlobalSymbols

  beforeCreate() {
    /*    const description = "Software Architecture Tutor";
    const image = require("../public/logo.png");
    let tag = document.createElement("meta");
    tag.setAttribute("description", description);
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("author", "António Rito Silva && Pedro Correia");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("image", image);
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("twitter:title", description);
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("twitter:description", description);
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("twitter:card", "summary");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("twitter:image:src", image);
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("name", "Software Architecture Tutor");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:site_name", "Software Architecture Tutor");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:title", "Software Architecture Tutor");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:description", "Software Architecture Tutor");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:image", image);
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:url", "http://");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:locale", "en_US");
    document.head.appendChild(tag);
    tag = document.createElement("meta");
    tag.setAttribute("og:type", "website");
    document.head.appendChild(tag);*/
  }

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

<style>
#app {
  text-align: center;
  color: #2c3e50;
  height: 100%;
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
