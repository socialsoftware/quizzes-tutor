<template>
  <div class="container">
    <password-card :title='TITLE' email='placeholder@email.com' @onSubmit='confirmRegistration'></password-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import PasswordCard from '@/components/auth/PasswordCard.vue';

@Component({
  components: { PasswordCard }
})
export default class RegistrationConfirmationView extends Vue {
	TITLE = 'Registration Confirmation'

	async created() {}
	
	confirmRegistration(password: string) {
		console.log(`Confirm Registration with password: ${password}`);
	}
}
</script>

<style lang="scss" scoped>
</style>
