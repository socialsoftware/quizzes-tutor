<template>
  <v-card class="mx-auto my-12" max-width="374">
    <v-card-title>Anonymize User</v-card-title>
    <v-card-text>
      <v-row align="center" class="mx-0">
        <v-text-field
          v-model="username"
          :counter="10"
          label="Username"
          required
        ></v-text-field>
      </v-row>
      <v-btn :disabled="username == ''" color="primary" @click="anonymize"
        >Anonymize</v-btn
      >
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';

const store = useStore();
const username = ref('');

const anonymize = async () => {
  if (confirm('Are you sure you want to anonymize the user?')) {
    store.setLoading();
    try {
      await RemoteServices.anonymizeUser(username.value);
    } catch (error) {
      store.setError(error as string);
    }
    store.clearLoading();

    username.value = '';
  }
};
</script>

<style lang="scss" scoped></style>
