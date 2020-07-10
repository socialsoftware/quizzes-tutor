<template>
  <v-card>
    <v-card-title>{{title}}</v-card-title>
    <v-card-text v-if="!success">
      <form>
        <v-text-field
          v-model="email"
          label="Email"
          disabled
          required
        ></v-text-field>
        <v-text-field
          v-model="password"
          :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
          :type="show1 ? 'text' : 'password'"
          label="Password"
          required
          @click:append="show1 = !show1"

        ></v-text-field>
        <v-text-field
          v-model="confirmPassword"
          :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
          :type="show2 ? 'text' : 'password'"
          label="Confirm Password"
          required
          :rules="[v => v == password || 'Passwords don\'t match']"
          @click:append="show2 = !show2"
        ></v-text-field>
        <v-btn color="blue darken-1" class="white--text" @click="submit">submit</v-btn>
      </form>
    </v-card-text>
    <v-card-text v-if="success">
      Success
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component
export default class PasswordCard extends Vue {
  @Prop({ required: true })
  title: string | undefined;
  @Prop({ required: true })
  email: string | undefined;

  success = false

  password = '';
  confirmPassword = '';

  show1 = false;
  show2 = false;

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
</style>
