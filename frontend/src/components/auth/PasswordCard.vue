<template>
  <v-card v-if="error == ''">
    <v-card-title>{{ title }}</v-card-title>
    <v-card-text v-if="!success">
      <form>
        <v-text-field
          v-model="username"
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
          color="blue darken-1"
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

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component
export default class PasswordCard extends Vue {
  @Prop({ required: true })
  title: string | undefined;
  @Prop({ required: true })
  username: string | undefined;
  @Prop({ required: true })
  error: string | undefined;
  @Prop({ required: true })
  success: boolean | undefined;

  password = '';
  confirmPassword = '';

  showPassword = false;
  showConfirmPassword = false;

  created() {}

  submit() {
    if (this.password == this.confirmPassword)
      this.$emit('onSubmit', this.password);
  }
}
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
