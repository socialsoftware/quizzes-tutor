<template>
  <v-alert
    v-model="dialog"
    type="error"
    close-text="Close Notification"
    dismissible
  >
    {{ messageList.join(', ') }}
  </v-alert>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useStore } from '@/store';

const store = useStore();

const dialog = computed({
  get: () => store.notification,
  set: (val: boolean) => {
    if (!val) {
      store.clearNotification();
    }
  }
});
const messageList = computed(() => store.notificationMessageList);
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
