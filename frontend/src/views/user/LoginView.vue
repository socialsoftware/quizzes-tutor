<template>
  <div class="container"></div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useStore } from '@/store';
import { useRoute, useRouter } from 'vue-router';

const store = useStore();
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  store.setLoading();
  if (route.query.error) {
    store.setError('Fenix authentication error');
    await router.push({ name: 'home' });
  } else {
    try {
      if (route.query.code) {
        await store.fenixLogin(route.query.code as string);
        await router.push({ name: 'courses' });
      }
    } catch (error) {
      store.setError(error as string);
      await router.push({ name: 'home' });
    }
  }
  store.clearLoading();
});
</script>

<style lang="scss" scoped>
.btns-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .v-btn {
    margin: 5px;
  }
}
</style>
