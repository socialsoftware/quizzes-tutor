<template>
  <v-alert v-model="dialog" type="error" close-text="Close Alert" closable>
    {{ errorMessage }}
  </v-alert>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useStore } from '@/store';

const store = useStore();

const dialog = computed({
  get: () => store.error,
  set: (val: boolean) => {
    if (!val) {
      store.clearError();
    }
  }
});
const errorMessage = computed(() => store.errorMessage);
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
