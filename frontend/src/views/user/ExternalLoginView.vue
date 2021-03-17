<template>
  <div class="container">
    <login-card @onSubmit="login"></login-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import LoginCard from '@/components/auth/LoginCard.vue';
import ExternalUser from '@/models/user/ExternalUser';
import RemoteServices from '../../services/RemoteServices';

@Component({
  components: { LoginCard },
})
export default class ExternalLoginView extends Vue {
  async created() {}

  async login(username: string, password: string) {
    const user = new ExternalUser();
    user.username = username;
    user.password = password;

    await this.$store.dispatch('loading');
    try {
      await this.$store.dispatch('externalLogin', user);
      await this.$router.push({ name: 'courses' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped></style>
