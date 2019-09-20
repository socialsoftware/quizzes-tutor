<template>
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
  position: absolute;
  width: calc(100% - 20px);
  margin: 10px !important;
}
</style>
