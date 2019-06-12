<template>
  <div>
    <p>Waiting for Login to process</p>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component
export default class HomeView extends Vue {
  async mounted() {
    if (this.$route.query.error) {
      console.log(this.$route.query.error_description);
      //TODO deal with error
    } else {
      console.log(this.$route.query.code);
      await this.$store.dispatch("login", this.$route.query.code);
      this.$router.push({ name: "home" });
    }
  }
}
</script>

<style></style>
