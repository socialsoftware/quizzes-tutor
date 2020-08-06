<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="userQuestionSubmissionsInfo"
      :search="search"
      :sort-by="['numQuestionSubmissions']"
      sort-desc
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            data-cy="Search"
          />
        </v-card-title>
      </template>
      <template v-slot:numQuestionSubmissions="{ item }">
        {{ item.numQuestionSubmissions }}
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import UserQuestionSubmissionInfo from '@/models/management/UserQuestionSubmissionInfo';

@Component
export default class AllUserQuestionSubmissionInfoView extends Vue {
  userQuestionSubmissionsInfo: UserQuestionSubmissionInfo[] = [];
  search = '';

  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left'
    },
    { text: 'Username', value: 'username', align: 'left' },
    {
      text: 'Number of Submissions',
      value: 'numQuestionSubmissions',
      align: 'left'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.userQuestionSubmissionsInfo] = await Promise.all([
        RemoteServices.getAllStudentsQuestionSubmissionsInfo()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
