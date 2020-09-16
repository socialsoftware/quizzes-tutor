<template>
  <v-card>
    <v-card-title>Login</v-card-title>
    <v-card-text>
      <form>
        <v-text-field v-model="email" label="Email" required></v-text-field>
        <v-text-field
          v-model="password"
          :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
          :type="show1 ? 'text' : 'password'"
          label="Password"
          required
          @click:append="show1 = !show1"
        ></v-text-field>
        <v-btn
          color="blue darken-1"
          class="white--text"
          :disabled="!(email !== '' && password !== '')"
          @click="submit"
          >login</v-btn
        >
      </form>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component
export default class LoginCard extends Vue {
  email = '';
  password = '';

  show = false;

  created() {}

  submit() {
    this.$emit('onSubmit', this.email, this.password);
  }
}
</script>

<style scoped lang="scss">
.v-card {
  width: 650px;
  margin: 32px auto;
}
</style>
