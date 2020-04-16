<template>
  <v-card class="table">
    <v-btn color="primary" @click="exportAll">Export ZIP</v-btn>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ImpExptView extends Vue {
  async exportAll() {
    try {
      await RemoteServices.exportAll();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped></style>
