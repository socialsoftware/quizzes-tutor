<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('close-password-dialog')"
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import Tournament from '@/models/user/Tournament';

const props = defineProps<{
  dialog: boolean;
  tournament: Tournament;
}>();

const emit = defineEmits(['close-password-dialog', 'enter-password']);

const store = useStore();

const joinTournament = ref<Tournament | null>(null);
const passwordFieldType = ref('password');
const password = ref('');

onMounted(async () => {
  joinTournament.value = props.tournament;
  password.value = '';
  store.setLoading();
  store.clearLoading();
});

const switchVisibility = async () => {
  passwordFieldType.value = passwordFieldType.value === 'password' ? 'text' : 'password';
};

const passwordEmit = async () => {
  emit('enter-password', password.value);
};
</script>
