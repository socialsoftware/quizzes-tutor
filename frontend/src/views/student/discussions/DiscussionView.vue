<template>
  <v-card max-width="1200" class="mx-auto my-7">
    <v-card-title>
      <b>Discussions</b>
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table :headers="headers" :items="discussions" :search="search">
      <template v-slot:item.title="{ item }">
        <div>
          {{ item.title }}
        </div>
      </template>

      <template v-slot:item.message="{ item }">
        <div>
          {{ item.message }}
        </div>
      </template>

      <template v-slot:item.available="{ item }">
        <div>
          {{ item.available }}
        </div>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import User from '@/models/user/User';
@Component
export default class DiscussionView extends Vue {
  discussions: Discussion[] = [];
  search: string = '';
  user: User = this.$store.getters.getUser;

  headers: object = [
    {
      text: 'Question',
      align: 'start',
      sortable: false,
      value: 'question.title'
    },
    { text: 'Message', value: 'message' },
    { text: 'Public', value: 'available' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.discussions] = await Promise.all([
        RemoteServices.getDiscussions(this.user.id)
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
