<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="userSubmissionsInfo"
      :search="search"
      :sort-by="['numSubmissions']"
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
      <template v-slot:numSubmissions="{ item }">
        {{ item.numSubmissions }}
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import UserSubmissionInfo from '@/models/management/UserSubmissionInfo';

@Component
export default class AllUserSubmissionInfoView extends Vue {
  userSubmissionsInfo: UserSubmissionInfo[] = [];
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
      value: 'numSubmissions',
      align: 'left'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.userSubmissionsInfo] = await Promise.all([
        RemoteServices.getAllStudentsSubmissionsInfo()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
