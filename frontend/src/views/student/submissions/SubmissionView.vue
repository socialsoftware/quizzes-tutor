<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="submissions"
      :search="search"
      :sort-by="['question.creationDate']"
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
            data-cy="Search"
            class="mx-2"
          />

          <v-spacer />
          <v-btn color="primary" dark @click="getSubmissions"
            >Refresh List</v-btn
          ><v-btn
            color="primary"
            dark
            @click="submitQuestion"
            data-cy="SubmitQuestion"
            >Submit Question</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.question.title="{ item }">
        <div @click="showSubmissionDialog(item)" class="clickableTitle">
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
          v-if="item.question.status === 'IN_REVISION'"
          :submission="item"
          :topics="topics"
          v-on:submission-changed-topics="onSubmissionChangedTopics"
        />
        <view-submission-topics v-else :submission="item" :topics="topics" />
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showSubmissionDialog(item)"
              data-cy="viewQuestion"
              >visibility</v-icon
            >
          </template>
          <span>Show Submission</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.question.status === 'IN_REVISION'">
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" @click="editSubmission(item)"
              >edit</v-icon
            >
          </template>
          <span>Edit Submission</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.question.status === 'IN_REVISION'">
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
    <edit-submission-dialog
      v-if="currentSubmission"
      v-model="editSubmissionDialog"
      :submission="currentSubmission"
      v-on:save-submission="onSaveSubmission"
    />
    <show-submission-dialog
      v-if="currentSubmission"
      v-model="submissionDialog"
      :submission="currentSubmission"
      v-on:close-show-question-dialog="onCloseShowSubmissionDialog"
    />
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on question's title to view
      submitted question and submission status.
    </footer>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ShowSubmissionDialog from '@/views/student/submissions/ShowSubmissionDialog.vue';
import EditSubmissionTopics from '@/views/student/submissions/EditSubmissionTopics.vue';
import ViewSubmissionTopics from '@/views/student/submissions/ViewSubmissionTopics.vue';
import EditSubmissionDialog from '@/views/student/submissions/EditSubmissionDialog.vue';
import Question from '@/models/management/Question';
import Submission from '@/models/management/Submission';
import Topic from '@/models/management/Topic';

@Component({
  components: {
    'show-submission-dialog': ShowSubmissionDialog,
    'edit-submission-topics': EditSubmissionTopics,
    'view-submission-topics': ViewSubmissionTopics,
    'edit-submission-dialog': EditSubmissionDialog
  }
})
export default class SubmissionView extends Vue {
  submissions: Submission[] = [];
  topics: Topic[] = [];
  currentSubmission: Submission | null = null;
  editSubmissionDialog: boolean = false;
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
      width: '50%%'
    },
    {
      text: 'Creation Date',
      value: 'question.creationDate',
      width: '150px',
      align: 'center'
    }
  ];

  @Watch('editSubmissionDialog')
  closeError() {
    if (!this.editSubmissionDialog) {
      this.currentSubmission = null;
    }
  }

  async created() {
    await this.getSubmissions();
  }

  async getSubmissions() {
    await this.$store.dispatch('loading');
    try {
      [this.submissions, this.topics] = await Promise.all([
        RemoteServices.getStudentSubmissions(),
        RemoteServices.getTopics()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string, question: Question) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
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

  submitQuestion() {
    let question = new Question();
    question.status = 'IN_REVISION';
    this.currentSubmission = new Submission();
    this.currentSubmission.courseExecutionId = this.$store.getters.getCurrentCourse.courseExecutionId;
    this.currentSubmission.question = question;
    this.editSubmissionDialog = true;
  }

  editSubmission(submission: Submission, e?: Event) {
    if (e) e.preventDefault();
    this.currentSubmission = submission;
    this.editSubmissionDialog = true;
  }

  async onSaveSubmission() {
    await this.$store.dispatch('loading');
    try {
      [this.submissions] = await Promise.all([
        RemoteServices.getStudentSubmissions()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    this.editSubmissionDialog = false;
    this.currentSubmission = null;
  }

  onSubmissionChangedTopics(questionId: Number, changedTopics: Topic[]) {
    let submission = this.submissions.find(
      (submission: Submission) => submission.question.id == questionId
    );
    if (submission) {
      submission.question.topics = changedTopics;
    }
  }

  showSubmissionDialog(submission: Submission) {
    this.currentSubmission = submission;
    this.submissionDialog = true;
  }

  onCloseShowSubmissionDialog() {
    this.submissionDialog = false;
  }

  async deleteSubmission(toDeleteSubmission: Submission) {
    if (
      toDeleteSubmission.id &&
      confirm('Are you sure you want to delete this submission?')
    ) {
      try {
        let [reviews] = await Promise.all([
          RemoteServices.getSubmissionReviews(toDeleteSubmission.id!)
        ]);
        if (reviews.length > 0) {
          console.log(reviews);
          await this.$store.dispatch(
            'error',
            'Error: Cannot delete submission that already has reviews'
          );
          return;
        }
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
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
