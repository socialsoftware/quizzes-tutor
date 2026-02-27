<template>
  <v-overlay v-model="loading" absolute class="align-center justify-center">
    <v-progress-circular indeterminate size="64" color="white" />
  </v-overlay>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useStore } from '@/store';

const store = useStore();

const loading = computed({
  get: () => store.loading,
  set: (val: boolean) => {
    if (!val) {
      store.clearLoading();
    }
  }
});
</script>
