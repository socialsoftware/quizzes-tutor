<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card class="mx-auto" max-width="500" outlined>
          <v-list-item
            style="border-bottom: 3px solid;border-bottom-color: #1e88e5"
          >
            <v-col>
              <v-list-item-content>
                <div class="overline mb-4"></div>
                <v-list-item-title class="headline mb-1">{{
                  info !== null ? info.name : 'Unknown user'
                }}</v-list-item-title>
                <v-list-item-subtitle>{{
                  info !== null ? info.username : 'Unknown user'
                }}</v-list-item-subtitle>
              </v-list-item-content>
            </v-col>
          </v-list-item>

          <v-list-item>
            Discussions created:
            {{ info !== null ? info.numDiscussions : 0 }}
          </v-list-item>
          <v-list-item>
            Discussions answered:
            {{ info !== null ? info.numAnsweredDiscussions : 0 }}
          </v-list-item>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-list-item-title>
            <v-list-item-title
              class="headline"
              style="background-color: #1976d2; color: white;padding: 10px; "
              >Answered Discussions</v-list-item-title
            >
          </v-list-item-title>
          <v-col>
            <v-list-item-content
              v-if="info !== null && info.numDiscussions !== 0"
            >
              <v-data-table :headers="headers" :items="discussions">
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
                  <v-chip v-else :color="'grey'" dark>{{
                    item.replies.length
                  }}</v-chip>
                </template>
                <template v-slot:item.action="{ item }">
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                      <v-icon
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
            </v-list-item-content>
            <v-list-item-content v-else>
              <v-list-item-title class="headline mb-1"
                ><b>No discussions to show</b></v-list-item-title
              >
            </v-list-item-content>
          </v-col>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import DashboardInfo from '@/models/management/DashboardInfo';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import User from '@/models/user/User';
import ShowDiscussionDialog from '@/views/student/discussions/ShowDiscussionDialog.vue';
@Component({
  components: {
    'show-discussion-dialog': ShowDiscussionDialog
  }
})
export default class DashboardView extends Vue {
  info: DashboardInfo | null = null;
  discussions: Discussion[] = [];
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
      text: 'Message',
      align: 'start',
      sortable: false,
      value: 'message'
    },
    { text: 'Public', value: 'available' },
    { text: 'Replies', value: 'replies.length' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.info = await RemoteServices.getDashboardInfo();
      [this.discussions] = await Promise.all([
        RemoteServices.getAnsweredDiscussions(
          this.$store.getters.getCurrentCourse.courseExecutionId,
          this.user.id!
        )
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
