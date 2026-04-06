<template>
  <v-app id="app">
    <top-bar />
    <v-main>
      <error-message />
      <notification />
      <loading />
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import axios from 'axios';
import TopBar from '@/components/TopBar.vue';
import ErrorMessage from '@/components/ErrorMessage.vue';
import Notification from '@/components/Notification.vue';
import Loading from '@/components/Loading.vue';
import { useStore } from '@/store';
import '@/assets/css/_global.scss';
import '@/assets/css/_scrollbar.scss';
import '@/assets/css/_question.scss';

import 'typeface-roboto';

const store = useStore();

axios.interceptors.response.use(undefined, (err) => {
  return new Promise(() => {
    if (err.status === 401 && err.config && !err.config.__isRetryRequest) {
      store.logout();
    }
    throw err;
  });
});
</script>

<style scoped>
#app {
  background-image: url('assets/img/background.jpg');
  background-position: center center;
  background-repeat: no-repeat;
  background-size: cover;
  background-attachment: fixed;
  min-height: 100vh;
  color: #2c3e50;
  display: flex;
  flex-direction: column;
  text-align: center;
  z-index: 1;
}

/*noinspection CssUnusedSymbol*/
.v-application__wrap {
  min-height: initial !important;
}
</style>
