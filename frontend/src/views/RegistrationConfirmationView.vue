<template>
  <div class="container">
    <password-card :title="TITLE" :email="email" :error="errorMsg" @onSubmit="confirmRegistration"></password-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import PasswordCard from '@/components/auth/PasswordCard.vue';
import ExternalUser from '@/models/user/ExternalUser';
import RemoteServices from '../services/RemoteServices';

@Component({
  components: { PasswordCard }
})
export default class RegistrationConfirmationView extends Vue {
  TITLE = 'Registration Confirmation';

  email: string = '';
  token: string = '';
  errorMsg: string = '';

  async created() {
    this.email = this.$route.query.email as string;
    this.token = this.$route.query.token as string;
    console.log(this.token);
    this.errorMsg = (this.email && this.token) ? '' : 'Invalid query';
  }

  async confirmRegistration(password: string) {
    const externalUser = new ExternalUser();
    externalUser.username = this.email;
    externalUser.password = password;
    externalUser.confirmationToken = this.token;
    const res = await RemoteServices.confirmRegistration(externalUser);
    if (res.state == 'INACTIVE') {
      this.errorMsg = 'Confirmation link has expired. A new email was sent';
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
