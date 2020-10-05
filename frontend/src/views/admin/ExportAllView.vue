<template> <div></div> </template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ExportAllView extends Vue {
  async created() {
    try {
      await RemoteServices.exportAll();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped></style>
