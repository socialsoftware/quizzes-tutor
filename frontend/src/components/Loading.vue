<template>
  <v-overlay :value="loading" absolute>
    <v-progress-circular indeterminate size="64" />
  </v-overlay>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component
export default class Loading extends Vue {
  loading: boolean = this.$store.getters.getLoading;

  created() {
    this.loading = this.$store.getters.getLoading;
    this.$store.watch(
      (state, getters) => getters.getLoading,
      () => {
        this.loading = this.$store.getters.getLoading;
      }
    );
  }

  @Watch('loading')
  closeError() {
    if (!this.loading) {
      this.$store.dispatch('clearLoading');
    }
  }
}
</script>
