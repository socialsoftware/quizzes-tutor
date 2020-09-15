<template>
  <v-card max-width="1200" class="mx-auto my-7">
    <v-card-title>
      <b>Forum</b>
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="discussions"
      :sort-by="'date'"
      :sort-desc="false"
      :search="search"
    >
      <template v-slot:item.available="{ item }">
        <v-switch
          style="flex: 1"
          v-model="item.available"
          :label="item.available ? 'Public' : 'Private'"
          @change="changeAvailability(item.id)"
        />
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
              >visibility</v-icon
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
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import ShowDiscussionDialog from './discussions/ShowDiscussionDialog.vue';

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
    { text: 'Creation Date', value: 'date' },
    { text: 'Public', value: 'available' },
    { text: 'Replies', value: 'replies.length' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.discussions] = await Promise.all([
        RemoteServices.getCourseExecutionDiscussions(this.executionId)
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

  async changeAvailability(id: number) {
    await this.$store.dispatch('loading');
    try {
      //needs to update discussion after this
      await RemoteServices.changeAvailability(id);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
