<template>
  <div></div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component
export default class HomeView extends Vue {
  async created() {
    await this.$store.dispatch("loading");
    if (this.$route.query.error) {
      await this.$store.dispatch("error", "Fenix authentication error");
      await this.$router.push({ name: "home" });
    } else {
      await this.$store.dispatch("login", this.$route.query.code);
      await this.$router.push({ name: "home" });
    }
    await this.$store.dispatch("clearLoading");
  }
}
</script>
