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
      <template v-slot:item.available="{ item }">
        <v-chip v-if="item.available === true" :color="'green'" dark
          >Yes</v-chip
        >
        <v-chip v-else :color="'red'" dark>No</v-chip>
      </template>
      <template v-slot:item.replies.length="{ item }">
        <v-chip v-if="item.replies === null" :color="'grey'" dark
        >0</v-chip
        >
        <v-chip v-else :color="'grey'" dark>{{ item.replies.length }}</v-chip>
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
      text: 'Question Title',
      align: 'start',
      sortable: false,
      value: 'question.title'
    },
    { text: 'Question Content', value: 'question.content' },
    { text: 'Message', value: 'message' },
    { text: 'Creation Date', value: 'date' },
    { text: 'Public', value: 'available' },
    { text: 'Replies', value: 'replies.length' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.discussions] = await Promise.all([
        RemoteServices.getDiscussions(this.user.id!)
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
