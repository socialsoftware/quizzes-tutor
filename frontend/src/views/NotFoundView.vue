<template>
  <div class="container">Not Found</div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

onMounted(() => {
  setTimeout(async () => {
    await router.push({ name: 'home' });
  }, 5000);
});
</script>

<style lang="scss" scoped></style>
