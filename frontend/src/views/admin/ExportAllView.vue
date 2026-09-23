<template><div></div></template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';

const store = useStore();

onMounted(async () => {
  store.setLoading();
  try {
    await RemoteServices.exportAll();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});
</script>

<style lang="scss" scoped></style>
