<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
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
          :items="this.course.courseExecutionUsers"
          :search="search"
          disable-pagination
          :hide-default-footer="true"
          :mobile-breakpoint="0"
          show-select
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

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import ExternalUser from '@/models/user/ExternalUser';
import User from '@/models/user/User';

@Component
export default class ViewUsersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Course, required: true }) readonly course!: Course;

  selectedUsers: User[] = [];
  search: string = '';
  items: object = [];
  headers: object = [
    { text: 'Username', value: 'username', align: 'left', width: '65%' },
    {
      text: 'Role',
      value: 'role',
      align: 'left',
      width: '15%',
    },
    {
      text: 'Active',
      value: 'active',
      align: 'left',
      width: '15%',
    },
  ];

  async created() {}
}
</script>
