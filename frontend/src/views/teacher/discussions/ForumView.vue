<template>
  <v-card max-width="1200" class="mx-auto my-7">
    <v-data-table
      :headers="headers"
      :items="discussions"
      :sort-by="'lastReplyDate'"
      :sort-desc="true"
      :search="search"
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Search"
            single-line
            hide-details
          />

          <v-spacer />
          <v-btn
            v-if="!showClosedDiscussions"
            color="primary"
            dark
            @click="toggleClosedDiscussions"
            >Show Closed Discussions</v-btn
          >
          <v-btn v-else color="primary" dark @click="toggleClosedDiscussions"
            >Hide Closed Discussions</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:item.available="{ item }">
        <v-chip v-if="item.available === true" :color="'green'" dark
          >Yes</v-chip
        >
        <v-chip v-else :color="'red'" dark>No</v-chip>
      </template>
      <template v-slot:item.closed="{ item }">
        <v-chip v-if="item.closed === true" :color="'green'" dark>Yes</v-chip>
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
              data-cy="showDiscussionButton"
              class="mr-2"
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
import { Component, Vue, Watch, Prop } from 'vue-property-decorator';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import ShowDiscussionDialog from '../../student/discussions/ShowDiscussionDialog.vue';

@Component({
  components: {
    'show-discussion-dialog': ShowDiscussionDialog
  }
})
export default class ForumView extends Vue {
  discussions: Discussion[] = [];
  search: string = '';
  executionId: number = this.$store.getters.getCurrentCourse.courseExecutionId;
  currentDiscussion: Discussion | null = null;
  discussionDialog: boolean = false;
  showClosedDiscussions: boolean = false;

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
        RemoteServices.getOpenCourseExecutionDiscussions(this.executionId)
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

  async toggleClosedDiscussions() {
    await this.$store.dispatch('loading');
    this.showClosedDiscussions = !this.showClosedDiscussions;
    try {
      if (this.showClosedDiscussions) {
        [this.discussions] = await Promise.all([
          RemoteServices.getCourseExecutionDiscussions(this.executionId)
        ]);
      } else {
        [this.discussions] = await Promise.all([
          RemoteServices.getOpenCourseExecutionDiscussions(this.executionId)
        ]);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
