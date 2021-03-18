<template>
  <div class="container">Not Found</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class NotFoundView extends Vue {
  created() {
    setTimeout(async () => {
      await this.$router.push({ name: 'home' });
    }, 5000);
  }
}
</script>

<style lang="scss" scoped></style>
