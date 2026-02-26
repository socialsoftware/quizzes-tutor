<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline"> Import Questions </span>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <v-icon color="blue darken-1" class="text-white" v-bind="props"
              >info</v-icon
            >
          </template>
          <v-card-text>
            <div>The questions will be created in this course</div>
          </v-card-text>
        </v-tooltip>
      </v-card-title>

      <v-file-input
        show-size
        dense
        small-chips
        label="Select a .xml file"
        v-model="chosenFile"
        accept=".xml"
      />

      <v-card-actions>
        <v-spacer />
        <v-btn
          class="text-white"
          color="red darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          class="text-white"
          color="green darken-1"
          :disabled="disabled"
          @click="uploadQuestions"
          data-cy="uploadFileButton"
          >Upload File</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';

defineProps<{
  dialog: boolean;
}>();

const emit = defineEmits(['close-dialog', 'questions-uploaded']);
const store = useStore();

const disabled = ref(false);
const chosenFile = ref<File | null>(null);

const uploadQuestions = async () => {
  store.setLoading();
  try {
    if (chosenFile.value != null) {
      disabled.value = true;
      let uploadedQuestions = await RemoteServices.importQuestions(chosenFile.value);
      confirm('File was uploaded!');
      emit('questions-uploaded', uploadedQuestions);
    } else {
      store.setError('In order to import questions, it must be selected a file');
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>
