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
        <span class="headline"> Users </span>
      </v-card-title>
      <v-card-text>
        <v-data-table
          v-model="selectedUsers"
          :headers="headers"
          :items="course?.courseExecutionUsers || []"
          :search="search"
          disable-pagination
          :hide-default-footer="true"
          :mobile-breakpoint="0"
          show-select
          return-object
        >
          <template v-slot:top>
            <v-card-title>
              <v-text-field
                v-model="search"
                append-icon="search"
                label="Search"
                class="mx-2"
              />
              <v-spacer />
              <v-spacer />
            </v-card-title>
          </template>
        </v-data-table>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Close</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="$emit('delete-users', selectedUsers)"
          data-cy="deleteSelectedUsersButton"
          >Delete Selected Users</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Course from '@/models/user/Course';
import User from '@/models/user/User';

const props = defineProps<{
  dialog: boolean;
  course: Course;
}>();

const emit = defineEmits(['close-dialog', 'delete-users', 'update:dialog']);

const selectedUsers = ref<User[]>([]);
const search = ref('');
const items = ref<object[]>([]);

const headers = [
  { title: 'Username', key: 'username', align: 'start', width: '65%' },
  { title: 'Role', key: 'role', align: 'start', width: '15%' },
  { title: 'Active', key: 'active', align: 'start', width: '15%' },
] as const;
</script>
