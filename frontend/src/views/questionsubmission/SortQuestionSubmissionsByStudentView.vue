<template>
  <v-card class="table">
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="search"
        label="Search Student"
        data-cy="Search"
        class="mx-2"
      />

      <v-spacer />
      <v-btn color="primary" dark @click="getUserQuestionSubmissionsInfo"
        >Refresh List</v-btn
      ><v-btn
        v-if="$store.getters.isTeacher"
        color="primary"
        dark
        to="/management/submissions"
        >Sort by Date</v-btn
      >
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="userQuestionSubmissionsInfo"
      :search="search"
      item-key="name"
      show-expand
      multi-sort
      single-expand
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:item="{ item, expand, isExpanded }">
        <tr
          v-bind:class="{ clickableRow: item.totalQuestionSubmissions > 0 }"
          @click="expand(!isExpanded && item.totalQuestionSubmissions > 0)"
        >
          <td>
            <v-icon v-if="!isExpanded">fa-angle-down</v-icon>
            <v-icon v-else>fa-angle-up</v-icon>
          </td>
          <td>{{ item.name }}</td>
          <td>
            <v-chip :color="item.numQuestionSubmissions.approved.color">{{
              item.numQuestionSubmissions.approved.num
            }}</v-chip>
          </td>
          <td>
            <v-chip :color="item.numQuestionSubmissions.rejected.color">{{
              item.numQuestionSubmissions.rejected.num
            }}</v-chip>
          </td>
          <td>
            <v-chip :color="item.numQuestionSubmissions.in_review.color">{{
              item.numQuestionSubmissions.in_review.num
            }}</v-chip>
          </td>
          <td>
            <v-chip :color="item.numQuestionSubmissions.in_revision.color">{{
              item.numQuestionSubmissions.in_revision.num
            }}</v-chip>
          </td>
          <td>
            <v-chip>{{ item.totalQuestionSubmissions }}</v-chip>
          </td>
        </tr>
      </template>
      <template v-slot:expanded-item="{ headers, item }">
        <td :colspan="headers.length">
          <v-data-table
            :headers="studentHeaders"
            :items="item.questionSubmissions"
            :sort-by="['question.creationDate']"
            sort-desc
            hide-default-footer
            class="studentSubmissions"
          >
            <template #item="{ item }">
              <tr>
                <td>
                  <v-tooltip bottom>
                    <template v-slot:activator="{ on }"
                      ><v-icon
                        class="mr-2 action-button"
                        v-on="on"
                        @click="showQuestionSubmissionDialog(item)"
                        data-cy="ViewSubmission"
                        >fa-comments</v-icon
                      >
                    </template>
                    <span>View Submission</span>
                  </v-tooltip>
                </td>
                <td>
                  <div
                    @click="showQuestionSubmissionDialog(item)"
                    class="clickableTitle"
                  >
                    {{ item.question.title }}
                  </div>
                </td>
                <td>
                  <v-chip :color="item.getStatusColor()" small>
                    <span>{{ item.getStatus() }}</span>
                  </v-chip>
                </td>
                <td>
                  <edit-question-submission-topics
                    :questionSubmission="item"
                    :topics="topics"
                    :readOnly="true"
                  />
                </td>
                <td>{{ item.question.creationDate }}</td>
              </tr>
            </template>
          </v-data-table>
        </td>
      </template>
    </v-data-table>
    <show-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="questionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
    />
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on student to
      see their question submissions
    </footer>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import UserQuestionSubmissionInfo from '@/models/management/UserQuestionSubmissionInfo';
import RemoteServices from '@/services/RemoteServices';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import ShowQuestionSubmissionDialog from '@/views/questionsubmission/ShowQuestionSubmissionDialog.vue';
import Topic from '@/models/management/Topic';
import EditQuestionSubmissionTopics from '@/views/questionsubmission/EditQuestionSubmissionTopics.vue';

@Component({
  components: {
    'show-question-submission-dialog': ShowQuestionSubmissionDialog,
    'edit-question-submission-topics': EditQuestionSubmissionTopics,
  },
})
export default class SortQuestionSubmissionsByStudentView extends Vue {
  userQuestionSubmissionsInfo: UserQuestionSubmissionInfo[] = [];
  topics: Topic[] = [];
  currentQuestionSubmission: QuestionSubmission | null = null;
  questionSubmissionDialog: boolean = false;
  search: string = '';
  studentHeaders = QuestionSubmission.questionSubmissionHeader.slice();
  headers = [
    {
      text: 'Student',
      value: 'name',
      align: 'center',
      width: '50%',
    },
    {
      text: 'Approved',
      value: 'numApprovedQuestionSubmissions',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Rejected',
      value: 'numRejectedQuestionSubmissions',
      align: 'center',
      width: '10%',
    },
    {
      text: 'In Review',
      value: 'numInReviewQuestionSubmissions',
      align: 'center',
      width: '10%',
    },
    {
      text: 'In Revision',
      value: 'numInRevisionQuestionSubmissions',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Total',
      value: 'totalQuestionSubmissions',
      align: 'center',
      width: '10%',
    },
  ];

  async created() {
    await this.getUserQuestionSubmissionsInfo();
  }

  async getUserQuestionSubmissionsInfo() {
    await this.$store.dispatch('loading');
    try {
      [this.userQuestionSubmissionsInfo, this.topics] = await Promise.all([
        RemoteServices.getAllStudentsSubmissionsInfo(),
        RemoteServices.getTopics(),
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showQuestionSubmissionDialog(questionSubmission: QuestionSubmission) {
    this.currentQuestionSubmission = questionSubmission;
    this.questionSubmissionDialog = true;
  }

  @Watch('questionSubmissionDialog')
  async onCloseShowQuestionSubmissionDialog() {
    if (!this.questionSubmissionDialog) {
      await this.getUserQuestionSubmissionsInfo();
    }
  }
}
</script>
<style lang="scss">
.clickableRow {
  cursor: pointer;
}
.studentSubmissions {
  border: 1px lightgrey solid;
}
</style>
