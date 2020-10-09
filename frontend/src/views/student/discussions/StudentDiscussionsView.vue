<template>
  <v-card max-width="1200" class="mx-auto my-7">
    <v-data-table
      :headers="headers"
      :items="discussions"
      :sort-by="'lastReplyDate'"
      :sort-desc="true"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title style="width: 50%">
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
          />
        </v-card-title>
      </template>
      <template v-slot:item.available="{ item }">
        <v-chip v-if="item.available === true" :color="'green'" dark
          >Yes</v-chip
        >
        <v-chip v-else :color="'red'" dark>No</v-chip>
      </template>
      <template v-slot:item.replies.length="{ item }">
        <v-chip v-if="item.replies === null" :color="'grey'" dark>0</v-chip>
        <v-chip v-else :color="'grey'" dark>{{ item.replies.length }}</v-chip>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="showDiscussionDialog(item)"
              >fas fa-comment-dots</v-icon
            >
          </template>
          <span>Show Discussion</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-discussion-dialog
      v-if="currentDiscussion"
      v-model="discussionDialog"
      :discussion="currentDiscussion"
      v-on:close-show-question-dialog="onCloseShowDiscussionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import User from '@/models/user/User';
import StudentShowDiscussionDialog from '@/views/student/discussions/StudentShowDiscussionDialog.vue';

@Component({
  components: {
    'show-discussion-dialog': StudentShowDiscussionDialog
  }
})
export default class StudentDiscussionsView extends Vue {
  discussions: Discussion[] = [];
  search: string = '';
  user: User = this.$store.getters.getUser;
  currentDiscussion: Discussion | null = null;
  discussionDialog: boolean = false;

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false
    },
    {
      text: 'Question Title',
      value: 'question.title'
    },
    { text: 'Question Content', value: 'question.content' },
    { text: 'Message', value: 'message' },
    { text: 'Last Reply Date', value: 'lastReplyDate' },
    { text: 'Public', value: 'available' },
    { text: 'Closed', value: 'closed' },
    { text: 'Replies', value: 'replies.length' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.discussions] = await Promise.all([
        RemoteServices.getDiscussionsByUserId(this.user.id!)
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  showDiscussionDialog(discussion: Discussion) {
    this.currentDiscussion = discussion;
    this.discussionDialog = true;
  }

  onCloseShowDiscussionDialog() {
    this.currentDiscussion = null;
    this.discussionDialog = false;
  }

  @Watch('discussionDialog')
  closeError() {
    if (!this.discussionDialog) {
      this.currentDiscussion = null;
    }
  }
}
</script>

<style scoped></style>
