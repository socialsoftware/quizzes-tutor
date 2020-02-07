<template>
  <v-container fluid>
    <v-card class="table">
      <v-row align="center">
        <v-col class="text-center" cols="12" sm="4">
          <div class="my-2">
            <v-btn color="primary" @click="exportAll">Export ZIP</v-btn>
          </div>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
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
