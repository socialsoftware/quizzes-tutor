<template>
  <div>
    <a :href="fenix_url">Fénix</a>
    <a href="/creator">Quiz-Creator</a>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component
export default class HomeView extends Vue {
  fenix_url: string =
    "https://fenix.tecnico.ulisboa.pt/oauth/userdialog?client_id=1132965128044622&redirect_uri=http://tolocalhost.com:8081/login";
}
</script>

<style></style>
