<template>
  <div>
    <p>Waiting for Login to process</p>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component
export default class HomeView extends Vue {
  // noinspection JSUnusedGlobalSymbols
  async mounted() {
    if (this.$route.query.error) {
      await this.$store.dispatch("error", "Fenix authentication error");
      await this.$router.push({ name: "home" });
    } else {
      await this.$store.dispatch("login", this.$route.query.code);
      await this.$router.push({ name: "home" });
    }
  }
}
</script>

<style></style>
