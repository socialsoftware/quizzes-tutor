<template>
  <v-card v-if="error == ''">
    <v-card-title>{{ title }}</v-card-title>
    <v-card-text v-if="!success">
      <form>
        <v-text-field
          :model-value="username"
          label="Username"
          disabled
          required
        ></v-text-field>
        <v-text-field
          v-model="password"
          :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPassword ? 'text' : 'password'"
          label="Password"
          required
          @click:append="showPassword = !showPassword"
        ></v-text-field>
        <v-text-field
          v-model="confirmPassword"
          :append-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showConfirmPassword ? 'text' : 'password'"
          label="Confirm Password"
          required
          :rules="[(v) => v == password || 'Passwords don\'t match']"
          @click:append="showConfirmPassword = !showConfirmPassword"
        ></v-text-field>
        <v-btn
          color="blue-darken-1"
          class="white--text"
          :disabled="!(password === confirmPassword && password != '')"
          @click="submit"
          >submit</v-btn
        >
      </form>
    </v-card-text>
    <v-card-text v-if="success">
      <span class="password-success">Success</span>
    </v-card-text>
  </v-card>
  <v-card v-else>
    <v-card-title>{{ error }}</v-card-title>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  title: string;
  username: string;
  error: string;
  success: boolean;
}>();

const emit = defineEmits(['onSubmit']);

const password = ref('');
const confirmPassword = ref('');
const showPassword = ref(false);
const showConfirmPassword = ref(false);

const submit = () => {
  if (password.value === confirmPassword.value) {
    emit('onSubmit', password.value);
  }
};
</script>

<style scoped lang="scss">
.v-card {
  width: 650px;
  margin: auto;
}
.password-success {
  display: block;
  font-size: 1.5rem;
  margin: 20px 0;
}
</style>
