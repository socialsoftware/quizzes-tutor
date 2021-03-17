<template><div></div></template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ExportAllView extends Vue {
  async created() {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.exportAll();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped></style>
