<template>
  <div class="container">
    <login-card @onSubmit="login"></login-card>
  </div>
</template>

<script setup lang="ts">
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import LoginCard from '@/components/auth/LoginCard.vue';
import ExternalUser from '@/models/user/ExternalUser';

const store = useStore();
const router = useRouter();

const login = async (username: string, password: string) => {
  const user = new ExternalUser();
  user.username = username;
  user.password = password;

  store.setLoading();
  try {
    await store.externalLogin(user);
    await router.push({ name: 'courses' });
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>

<style lang="scss" scoped></style>
