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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useStore } from '@/store';
import PasswordCard from '@/components/auth/PasswordCard.vue';
import ExternalUser from '@/models/user/ExternalUser';
import RemoteServices from '@/services/RemoteServices';

const route = useRoute();
const store = useStore();

const TITLE = 'Registration Confirmation';

const username = ref<string>('');
const token = ref<string>('');
const errorMsg = ref<string>('');
const success = ref<boolean>(false);

onMounted(() => {
  username.value = (route.query.username as string) || '';
  token.value = (route.query.token as string) || '';
  errorMsg.value = username.value && token.value ? '' : 'Invalid query';
});

const confirmRegistration = async (password: string) => {
  const externalUser = new ExternalUser();
  externalUser.username = username.value;
  externalUser.password = password;
  externalUser.confirmationToken = token.value;

  try {
    const user = await RemoteServices.confirmRegistration(externalUser);
    if (user.active) {
      success.value = true;
    } else {
      errorMsg.value = 'Confirmation link has expired. A new email was sent';
    }
  } catch (error) {
    store.setError(error as string);
  }
};
</script>

<style lang="scss" scoped></style>
