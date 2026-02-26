<template>
  <v-card>
    <v-card-title>Login</v-card-title>
    <v-card-text>
      <form>
        <v-text-field
          v-model="username"
          label="Username"
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
        <v-btn
          color="blue darken-1"
          class="white--text"
          :disabled="!(username !== '' && password !== '')"
          @click="submit"
          >login</v-btn
        >
      </form>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const emit = defineEmits(['onSubmit']);

const username = ref('');
const password = ref('');
const showPassword = ref(false);

const submit = () => {
  emit('onSubmit', username.value, password.value);
};
</script>

<style scoped lang="scss">
.v-card {
  width: 650px;
  margin: 32px auto;
}
</style>
