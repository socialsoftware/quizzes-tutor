<template>
    <v-alert v-model="dialog" type="error" close-text="Close Notification" dismissible>
      {{ message }}
    </v-alert>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component
export default class Notification extends Vue {
  dialog: boolean = this.$store.getters.getNotification;
  message: string = this.$store.getters.getNotificationMessage;

  created() {
    this.dialog = this.$store.getters.getNotification;
    this.message = this.$store.getters.getNotificationMessage;
    this.$store.watch(
      (state, getters) => getters.getNotification,
      () => {
        this.dialog = this.$store.getters.getNotification;
				this.message = this.$store.getters.getNotificationMessage;
      }
    );
	}

  @Watch('dialog')
  closeNotification() {
    if (!this.dialog) {
      this.$store.dispatch('clearNotification');
    }
  }
}
</script>

<style scoped lang="scss">
.v-alert {
  z-index: 9999;
  position: absolute;
  left: 20px;
  top: 80px;
  width: calc(100% - 40px);
}
</style>
