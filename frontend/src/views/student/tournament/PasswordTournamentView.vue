<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-password-dialog')"
    @keydown.esc="$emit('close-password-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          <b>Password</b>
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-row>
              <v-text-field
                :type="passwordFieldType"
                v-model="password"
                label="Password"
                @keyup.enter="passwordEmit"
                data-cy="Password"
              >
                <template slot="append">
                  <v-icon medium class="mr-2" @click="switchVisibility()"
                    >visibility</v-icon
                  >
                </template>
              </v-text-field>
            </v-row>
            <v-card-actions>
              <v-spacer />
              <v-btn color="primary" @click="$emit('close-password-dialog')"
                >Cancel</v-btn
              >
              <v-btn
                color="primary"
                @click="passwordEmit"
                data-cy="joinPrivateTournament"
                >Join</v-btn
              >
            </v-card-actions>
          </v-layout>
        </v-container>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Tournament from '@/models/user/Tournament';

@Component
export default class PasswordTournamentView extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  joinTournament!: Tournament;
  passwordFieldType: string = 'password';
  password: string = '';

  async created() {
    this.joinTournament = this.tournament;
    this.password = '';
    await this.$store.dispatch('loading');
    await this.$store.dispatch('clearLoading');
  }

  async switchVisibility() {
    this.passwordFieldType =
      this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  async passwordEmit() {
    this.$emit('enter-password', this.password);
  }
}
</script>
