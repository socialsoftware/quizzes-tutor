<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="submissions"
      :search="search"
      :sort-by="['question.creationDate']"
      sort-desc
      item-key="id"
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
          />
        </v-card-title>
      </template>

      <template v-slot:item.question.title="{ item }">
        <div
          @click="showSubmissionDialog(item)"
          class="clickableTitle"
          data-cy="ViewSubmission"
        >
          {{ item.question.title }}
        </div>
      </template>
      <template v-slot:item.question.status="{ item }">
        <v-chip :color="getStatusColor(item.question.status)" small>
          <span>{{ item.question.status.replace('_', ' ') }}</span>
        </v-chip>
      </template>
      <template v-slot:item.question.topics="{ item }">
        <edit-submission-topics
          v-if="item.question.status !== 'REJECTED'"
          :submission="item"
          :topics="topics"
          v-on:submission-changed-topics="onSubmissionChangedTopics"
        />
        <view-submission-topics v-else :submission="item" :topics="topics" />
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" @click="showSubmissionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Submission</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              color="red"
              @click="deleteSubmission(item)"
              data-cy="deleteSubmission"
              >delete</v-icon
            >
          </template>
          <span>Delete Submission</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-submission-dialog
      v-if="currentSubmission"
      v-model="submissionDialog"
      :submission="currentSubmission"
    />
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on question's title to view
      submitted question and submission status.
    </footer>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { ISOtoString } from '@/services/ConvertDateService';
import Question from '@/models/management/Question';
import Submission from '@/models/management/Submission';
import ShowSubmissionDialog from '@/views/teacher/submissions/ShowSubmissionDialog.vue';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import EditSubmissionTopics from '@/views/teacher/submissions/EditSubmissionTopics.vue';
import ViewSubmissionTopics from '@/views/teacher/submissions/ViewSubmissionTopics.vue';

@Component({
  components: {
    'show-submission-dialog': ShowSubmissionDialog,
    'edit-submission-topics': EditSubmissionTopics,
    'view-submission-topics': ViewSubmissionTopics
  }
})
export default class SubmissionView extends Vue {
  submissions: Submission[] = [];
  topics: Topic[] = [];
  currentSubmission: Submission | null = null;
  submissionDialog: boolean = false;
  search: string = '';

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false
    },
    { text: 'Title', value: 'question.title', align: 'center', width: '50%' },
    { text: 'Submitted by', value: 'name', align: 'center', width: '10%' },
    {
      text: 'Status',
      value: 'question.status',
      align: 'center',
      width: '150px'
    },
    {
      text: 'Topics',
      value: 'question.topics',
      align: 'center',
      sortable: false,
      width: '50%'
    },
    {
      text: 'Creation Date',
      value: 'question.creationDate',
      width: '150px',
      align: 'center'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.submissions, this.topics] = await Promise.all([
        RemoteServices.getCourseExecutionSubmissions(),
        RemoteServices.getTopics()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string, question: Question) {
    return (
      search != null &&
      JSON.stringify(question)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  getStatusColor(status: string) {
    if (status === 'AVAILABLE') return 'green';
    else if (status === 'DISABLED') return 'orange';
    else if (status === 'REJECTED') return 'red';
    else if (status === 'IN_REVISION') return 'yellow';
    else if (status === 'IN_REVIEW') return 'blue';
  }

  onSubmissionChangedTopics(questionId: Number, changedTopics: Topic[]) {
    let submission = this.submissions.find(
      (submission: Submission) => submission.question.id == questionId
    );
    if (submission) {
      submission.question.topics = changedTopics;
    }
  }

  async showSubmissionDialog(submission: Submission) {
    await this.$store.dispatch('loading');
    try {
      this.currentSubmission = submission;
      if (this.isReviewable(this.currentSubmission)) {
        await RemoteServices.toggleInReviewStatus(
          submission!.question.id!,
          true
        );
      }
      this.submissionDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('submissionDialog')
  async onCloseShowSubmissionDialog() {
    if (!this.submissionDialog) {
      await this.$store.dispatch('loading');
      try {
        [this.submissions] = await Promise.all([
          RemoteServices.getCourseExecutionSubmissions()
        ]);
        this.submissionDialog = false;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async deleteSubmission(toDeleteSubmission: Submission) {
    if (
      toDeleteSubmission.id &&
      confirm('Are you sure you want to delete this submission?')
    ) {
      try {
        let questionId = toDeleteSubmission.question.id;
        if (questionId != null) await RemoteServices.deleteQuestion(questionId);
        this.submissions = this.submissions.filter(
          submission => submission.question.id != questionId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  isReviewable(submission: Submission) {
    return (
      submission.question.status == 'IN_REVISION' ||
      submission.question.status == 'IN_REVIEW'
    );
  }
}
</script>

<style lang="scss" scoped></style>
