<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questionSubmissions"
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
          <v-btn color="primary" dark @click="getQuestionSubmissions"
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
        <div @click="showQuestionSubmissionDialog(item)" class="clickableTitle">
          {{ item.question.title }}
        </div>
      </template>
      <template v-slot:item.question.status="{ item }">
        <v-chip :color="getStatusColor(item.question.status)" small>
          <span>{{ item.question.status.replace('_', ' ') }}</span>
        </v-chip>
      </template>
      <template v-slot:item.question.topics="{ item }">
        <edit-question-submission-topics
          v-if="item.question.status === 'IN_REVISION'"
          :questionSubmission="item"
          :topics="topics"
          v-on:submission-changed-topics="onQuestionSubmissionChangedTopics"
        />
        <view-question-submission-topics
          v-else
          :questionSubmission="item"
          :topics="topics"
        />
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showQuestionSubmissionDialog(item)"
              data-cy="viewQuestion"
              >visibility</v-icon
            >
          </template>
          <span>Show Question Submission</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.question.status === 'IN_REVISION'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="editQuestionSubmission(item)"
              data-cy="EditSubmission"
              >edit</v-icon
            >
          </template>
          <span>Edit Question Submission</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.question.status === 'IN_REVISION'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              color="red"
              @click="deleteQuestionSubmission(item)"
              data-cy="deleteSubmission"
              >delete</v-icon
            >
          </template>
          <span>Delete Question Submission</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="editQuestionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
      v-on:save-submission="onSaveQuestionSubmission"
    />
    <show-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="questionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
      v-on:close-show-question-dialog="onCloseShowQuestionSubmissionDialog"
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
import ShowQuestionSubmissionDialog from '@/views/student/question-submissions/ShowQuestionSubmissionDialog.vue';
import Question from '@/models/management/Question';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Topic from '@/models/management/Topic';
import EditQuestionSubmissionDialog from '@/views/student/question-submissions/EditQuestionSubmissionDialog.vue';
import EditQuestionSubmissionTopics from '@/views/teacher/question-submissions/EditQuestionSubmissionTopics.vue';
import ViewQuestionSubmissionTopics from '@/views/teacher/question-submissions/ViewQuestionSubmissionTopics.vue';

@Component({
  components: {
    'show-question-submission-dialog': ShowQuestionSubmissionDialog,
    'edit-question-submission-topics': EditQuestionSubmissionTopics,
    'view-question-submission-topics': ViewQuestionSubmissionTopics,
    'edit-question-submission-dialog': EditQuestionSubmissionDialog
  }
})
export default class QuestionSubmissionView extends Vue {
  questionSubmissions: QuestionSubmission[] = [];
  topics: Topic[] = [];
  currentQuestionSubmission: QuestionSubmission | null = null;
  editQuestionSubmissionDialog: boolean = false;
  questionSubmissionDialog: boolean = false;
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

  @Watch('editQuestionSubmissionDialog')
  closeError() {
    if (!this.editQuestionSubmissionDialog) {
      this.currentQuestionSubmission = null;
    }
  }

  async created() {
    await this.getQuestionSubmissions();
  }

  async getQuestionSubmissions() {
    await this.$store.dispatch('loading');
    try {
      [this.questionSubmissions, this.topics] = await Promise.all([
        RemoteServices.getStudentQuestionSubmissions(),
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
    this.currentQuestionSubmission = new QuestionSubmission();
    this.currentQuestionSubmission.courseExecutionId = this.$store.getters.getCurrentCourse.courseExecutionId;
    this.currentQuestionSubmission.userId = this.$store.getters.getUser.id;
    this.currentQuestionSubmission.question = question;
    this.editQuestionSubmissionDialog = true;
  }

  editQuestionSubmission(submission: QuestionSubmission, e?: Event) {
    if (e) e.preventDefault();
    this.currentQuestionSubmission = submission;
    this.editQuestionSubmissionDialog = true;
  }

  async onSaveQuestionSubmission() {
    await this.$store.dispatch('loading');
    try {
      [this.questionSubmissions] = await Promise.all([
        RemoteServices.getStudentQuestionSubmissions()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    this.editQuestionSubmissionDialog = false;
    this.currentQuestionSubmission = null;
  }

  onQuestionSubmissionChangedTopics(
    questionId: Number,
    changedTopics: Topic[]
  ) {
    let questionSubmission = this.questionSubmissions.find(
      (questionSubmission: QuestionSubmission) =>
        questionSubmission.question.id == questionId
    );
    if (questionSubmission) {
      questionSubmission.question.topics = changedTopics;
    }
  }

  showQuestionSubmissionDialog(questionSubmission: QuestionSubmission) {
    this.currentQuestionSubmission = questionSubmission;
    this.questionSubmissionDialog = true;
  }

  onCloseShowQuestionSubmissionDialog() {
    this.questionSubmissionDialog = false;
  }

  async deleteQuestionSubmission(
    toDeleteQuestionSubmission: QuestionSubmission
  ) {
    if (
      toDeleteQuestionSubmission.id &&
      confirm('Are you sure you want to delete this submission?')
    ) {
      try {
        let [reviews] = await Promise.all([
          RemoteServices.getQuestionSubmissionReviews(
            toDeleteQuestionSubmission.id!
          )
        ]);
        if (reviews.length > 0) {
          await this.$store.dispatch(
            'error',
            'Error: Cannot delete submission that already has reviews'
          );
          return;
        }
        await RemoteServices.deleteSubmittedQuestion(
          toDeleteQuestionSubmission.id
        );
        this.questionSubmissions = this.questionSubmissions.filter(
          questionSubmission =>
            questionSubmission.question.id !=
            toDeleteQuestionSubmission.question.id
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
