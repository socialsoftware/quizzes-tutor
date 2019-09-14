<template>
  <div id="app">
    <div class="img-container"></div>
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
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.img-container {
  position: absolute;
  background: white;
  overflow: hidden;
  top: 0;
  margin: 0 !important;
  z-index: -1;
  min-height: 100vh;
  width: 100vw;
  height: 100%;
}
.img-container:before {
  content: " ";
  display: block;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  opacity: 0.7;
  background-image: url("./assets/01.jpg");
  background-repeat: no-repeat;
  background-position: 0 0;
  -ms-background-size: cover;
  -o-background-size: cover;
  -moz-background-size: cover;
  -webkit-background-size: cover;
  background-size: cover;
}

/*noinspection CssUnusedSymbol*/
.application--wrap {
  min-height: initial !important;
}
</style>
