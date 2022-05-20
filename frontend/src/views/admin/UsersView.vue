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

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class UsersView extends Vue {
  username: String = '';

  async anonymize() {
    if (confirm('Are you sure you want to anonymize the user?')) {
      await this.$store.dispatch('loading');
      try {
        await RemoteServices.anonymizeUser(this.username);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');

      this.username = '';
    }
  }
}
</script>

<style lang="scss" scoped></style>
