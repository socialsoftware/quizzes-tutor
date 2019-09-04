<template>
  <v-app id="inspire">
    <v-alert
      v-model="error"
      dismissible
      border="top"
      elevation="2"
      colored-border
      type="error"
      dense
      transition="scale-transition"
    >
      {{ errorMessage }}
    </v-alert>
  </v-app>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";

@Component
export default class MessageBar extends Vue {
  error: boolean = this.$store.getters.getError;
  errorMessage: string = this.$store.getters.getErrorMessage;

  mounted() {
    this.$store.watch(
      (state, getters) => getters.getError,
      (newValue, oldValue) => {
        this.error = this.$store.getters.getError;
        this.errorMessage = this.$store.getters.getErrorMessage;
      }
    );
  }

  @Watch("error")
  closeError() {
    if (!this.error) {
      this.$store.dispatch("clearError");
    }
  }
}
</script>

<style scoped lang="scss">
.v-alert {
  margin: 10px !important;
}
</style>
