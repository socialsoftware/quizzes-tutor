<template>
  <v-dialog v-model="dialog">
    <v-alert v-model="dialog" type="error" close-text="Close Alert" dismissible>
      {{ errorMessage }}
    </v-alert>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";

@Component
export default class MessageBar extends Vue {
  dialog: boolean = this.$store.getters.getError;
  errorMessage: string = this.$store.getters.getErrorMessage;

  // noinspection JSUnusedGlobalSymbols
  mounted() {
    this.$store.watch(
      (state, getters) => getters.getError,
      () => {
        this.dialog = this.$store.getters.getError;
        this.errorMessage = this.$store.getters.getErrorMessage;
        console.log(2, this.dialog, this.$store.getters.getError);
      }
    );
  }

  @Watch("dialog")
  closeError() {
    console.log(1, this.dialog, this.$store.getters.getError);
    if (!this.dialog) {
      this.$store.dispatch("clearError");
    }
    console.log(3, this.dialog, this.$store.getters.getError);
  }
}
</script>

<style scoped lang="scss">
/*https://github.com/vuetifyjs/vuetify/issues/9175*/
.v-dialog__container {
  display: unset;
}

.v-alert {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 80px;
  width: calc(100% - 40px);
}
</style>
