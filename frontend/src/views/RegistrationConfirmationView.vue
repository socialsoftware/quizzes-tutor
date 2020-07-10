<template>
  <div class="container">
    <password-card :title='TITLE' :email='email' @onSubmit='confirmRegistration'></password-card>
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
    
    email: string | string[] = "";

	async created() {
        this.email = this.$route.query.email;
    }
	
	async confirmRegistration(password: string) {
        const externalUser = new ExternalUser();
        externalUser.username = this.email;
        externalUser.password = password;
        const res = await RemoteServices.confirmRegistration(externalUser);
	}
}
</script>

<style lang="scss" scoped>
</style>
