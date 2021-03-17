<template>
  <div class="container">
    <password-card
      :title="TITLE"
      :username="username"
      :error="errorMsg"
      :success="success"
      @onSubmit="confirmRegistration"
    ></password-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import PasswordCard from '@/components/auth/PasswordCard.vue';
import ExternalUser from '@/models/user/ExternalUser';
import RemoteServices from '../../services/RemoteServices';

@Component({
  components: { PasswordCard },
})
export default class RegistrationConfirmationView extends Vue {
  TITLE = 'Registration Confirmation';

  username: string = '';
  token: string = '';
  errorMsg: string = '';
  success: boolean = false;

  async created() {
    this.username = this.$route.query.username as string;
    this.token = this.$route.query.token as string;
    this.errorMsg = this.username && this.token ? '' : 'Invalid query';
  }

  async confirmRegistration(password: string) {
    const externalUser = new ExternalUser();
    externalUser.username = this.username;
    externalUser.password = password;
    externalUser.confirmationToken = this.token;

    try {
      const user = await RemoteServices.confirmRegistration(externalUser);
      if (user.active) {
        this.success = true;
      } else {
        this.errorMsg = 'Confirmation link has expired. A new email was sent';
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped></style>
