<template>
  <div class="container"></div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class LoginView extends Vue {
  async created() {
    await this.$store.dispatch('loading');
    if (this.$route.query.error) {
      await this.$store.dispatch('error', 'Fenix authentication error');
      await this.$router.push({ name: 'home' });
    } else {
      try {
        await this.$store.dispatch('fenixLogin', this.$route.query.code);
        await this.$router.push({ name: 'courses' });
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.btns-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .v-btn {
    margin: 5px;
  }
}
</style>
