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
            v-if="$store.getters.isTeacher"
            color="primary"
            dark
            to="/management/submissions/students"
            >Sort by Students</v-btn
          ><v-btn
            v-if="$store.getters.isStudent"
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
      <template v-slot:item.status="{ item }">
        <v-chip :color="item.getStatusColor()" small>
          <span>{{ item.status.replace('_', ' ') }}</span>
        </v-chip>
      </template>
      <template v-slot:item.question.topics="{ item }">
        <edit-question-submission-topics
          :questionSubmission="item"
          :topics="topics"
          :key="topicsComponentKey"
          v-on:submission-changed-topics="onQuestionSubmissionChangedTopics"
        />
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showQuestionSubmissionDialog(item)"
              data-cy="ViewSubmission"
              >question_answer</v-icon
            >
          </template>
          <span>View Submission Status</span>
        </v-tooltip>
        <v-tooltip
          bottom
          v-if="$store.getters.isStudent && item.isInRevision()"
        >
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
        <v-tooltip
          bottom
          v-if="$store.getters.isStudent && item.isInRevision()"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              color="red"
              @click="deleteQuestionSubmission(item)"
              data-cy="DeleteSubmission"
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
import Question from '@/models/management/Question';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Topic from '@/models/management/Topic';
import ShowQuestionSubmissionDialog from '@/views/questionsubmission/ShowQuestionSubmissionDialog.vue';
import EditQuestionSubmissionDialog from '@/views/questionsubmission/EditQuestionSubmissionDialog.vue';
import EditQuestionSubmissionTopics from '@/views/questionsubmission/EditQuestionSubmissionTopics.vue';

@Component({
  components: {
    'show-question-submission-dialog': ShowQuestionSubmissionDialog,
    'edit-question-submission-topics': EditQuestionSubmissionTopics,
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
  topicsComponentKey: number = 0;
  headers = QuestionSubmission.questionSubmissionHeader.slice();

  @Watch('editQuestionSubmissionDialog')
  closeError() {
    if (!this.editQuestionSubmissionDialog) {
      this.currentQuestionSubmission = null;
    }
  }

  async created() {
    if (this.$store.getters.isTeacher) {
      this.headers.splice(2, 0, {
        text: 'Submitted by',
        value: 'name',
        align: 'center',
        width: '50%'
      });
    }
    await this.getQuestionSubmissions();
  }

  async getQuestionSubmissions() {
    await this.$store.dispatch('loading');
    try {
      [this.questionSubmissions, this.topics] = this.$store.getters.isStudent
        ? await Promise.all([
            RemoteServices.getStudentQuestionSubmissions(),
            RemoteServices.getTopics()
          ])
        : await Promise.all([
            RemoteServices.getCourseExecutionQuestionSubmissions(),
            RemoteServices.getTopics()
          ]);
      this.topicsComponentKey += 1;
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

  submitQuestion() {
    let question = new Question();
    question.status = 'SUBMITTED';
    this.currentQuestionSubmission = new QuestionSubmission();
    this.currentQuestionSubmission.courseExecutionId = this.$store.getters.getCurrentCourse.courseExecutionId;
    this.currentQuestionSubmission.submitterId = this.$store.getters.getUser.id;
    this.currentQuestionSubmission.question = question;
    this.editQuestionSubmissionDialog = true;
  }

  editQuestionSubmission(submission: QuestionSubmission, e?: Event) {
    if (e) e.preventDefault();
    this.currentQuestionSubmission = submission;
    this.editQuestionSubmissionDialog = true;
  }

  async onSaveQuestionSubmission(questionSubmission: QuestionSubmission) {
    this.questionSubmissions = this.questionSubmissions.filter(
      qs => qs.id !== questionSubmission.id
    );
    this.questionSubmissions.unshift(questionSubmission);
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

  async showQuestionSubmissionDialog(questionSubmission: QuestionSubmission) {
    this.currentQuestionSubmission = questionSubmission;

    if (this.$store.getters.isTeacher) {
      await this.$store.dispatch('loading');
      try {
        if (this.currentQuestionSubmission.isInDiscussion()) {
          try {
            await RemoteServices.toggleInReviewStatus(
              questionSubmission!.id!,
              true
            );
          } catch (error) {
            await this.$store.dispatch('error', error);
          }
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    this.questionSubmissionDialog = true;
  }

  @Watch('questionSubmissionDialog')
  async onCloseShowQuestionSubmissionDialog() {
    if (!this.questionSubmissionDialog) {
      await this.getQuestionSubmissions();
      this.questionSubmissionDialog = false;
    }
  }

  async deleteQuestionSubmission(
    toDeleteQuestionSubmission: QuestionSubmission
  ) {
    if (
      toDeleteQuestionSubmission.id &&
      confirm('Are you sure you want to delete this submission?')
    ) {
      try {
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
