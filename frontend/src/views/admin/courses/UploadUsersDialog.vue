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
        <span class="headline"> Upload Users </span>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <v-icon color="blue darken-1" dark v-bind="props"
              >info</v-icon
            >
          </template>
          <v-card-text>
            <div>The file to upload must follow the rule:</div>
            <div>email,name,['student'|'teacher']</div>
            <div>
              When omitted the third column, by default is considered a student
            </div>
          </v-card-text>
        </v-tooltip>
      </v-card-title>

      <v-file-input
        show-size
        dense
        small-chips
        label="Select a .csv file"
        v-model="chosenFile"
        accept=".csv"
      />

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="red darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="green darken-1"
          @click="uploadUsers(course)"
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
import Course from '@/models/user/Course';

const props = defineProps<{
  dialog: boolean;
  course: Course;
}>();

const emit = defineEmits(['close-dialog', 'users-uploaded', 'update:dialog']);
const store = useStore();

const chosenFile = ref<File | null>(null);

const uploadUsers = async (course: Course) => {
  try {
    if (course.courseExecutionId != null && chosenFile.value != null) {
      let updatedCourse = await RemoteServices.registerExternalUsersCsvFile(
        chosenFile.value,
        course.courseExecutionId
      );
      confirm('File was uploaded!');

      emit('users-uploaded', updatedCourse);
    } else {
      store.setError('In order to upload users, it must be selected a file');
    }
  } catch (error) {
    store.setError(error as string);
  }
};
</script>
