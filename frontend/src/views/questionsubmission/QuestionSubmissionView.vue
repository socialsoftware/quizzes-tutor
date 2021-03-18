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
            @click="newSubmission"
            data-cy="NewSubmission"
            >New Submission</v-btn
          >
        </v-card-title>
      </template>

      <template #item="{ item }">
        <tr v-bind:class="{ unread: hasUnreadReviews(item) }">
          <td id="actions">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  v-if="hasUnreadReviews(item)"
                  class="unread-icon action-button"
                  v-on="on"
                  @click="showQuestionSubmissionDialog(item)"
                  data-cy="ViewSubmission"
                  color="white"
                  >fa-comment-dots</v-icon
                ><v-icon
                  v-else
                  class="mr-2 action-button"
                  v-on="on"
                  @click="showQuestionSubmissionDialog(item)"
                  data-cy="ViewSubmission"
                  >fa-comments</v-icon
                >
              </template>
              <span>View Submission</span>
            </v-tooltip>
            <v-tooltip
              bottom
              v-if="$store.getters.isStudent && item.isInRevision()"
            >
              <template v-slot:activator="{ on }">
                <v-icon
                  :class="[
                    'action-button',
                    { 'unread-icon': hasUnreadReviews(item) },
                  ]"
                  v-on="on"
                  @click="editQuestionSubmission(item)"
                  data-cy="EditSubmission"
                  :color="hasUnreadReviews(item) ? 'white' : ''"
                  >edit</v-icon
                >
              </template>
              <span>Edit Submission</span>
            </v-tooltip>
            <v-tooltip
              bottom
              v-if="$store.getters.isStudent && item.isInRevision()"
            >
              <template v-slot:activator="{ on }">
                <v-icon
                  v-bind:class="[
                    'action-button',
                    { 'unread-icon': hasUnreadReviews(item) },
                  ]"
                  v-on="on"
                  color="red"
                  @click="deleteQuestionSubmission(item)"
                  data-cy="DeleteSubmission"
                  >delete</v-icon
                >
              </template>
              <span>Delete Submission</span>
            </v-tooltip>
          </td>
          <td id="id">
            <div
              @click="showQuestionSubmissionDialog(item)"
              class="clickableId"
            >
              <v-layout justify-center>
                {{ item.id }}
              </v-layout>
            </div>
          </td>
          <td id="title">
            <div
              @click="showQuestionSubmissionDialog(item)"
              class="clickableTitle"
            >
              <v-layout justify-center>
                {{ item.question.title }}
              </v-layout>
            </div>
          </td>
          <td id="submittedBy" v-if="$store.getters.isTeacher">
            {{ item.name }}
          </td>
          <td id="status">
            <v-chip :color="item.getStatusColor()" small>
              <span>{{ item.getStatus() }}</span>
            </v-chip>
          </td>
          <td id="topics">
            <edit-question-submission-topics
              :questionSubmission="item"
              :topics="topics"
              :key="topicsComponentKey"
              v-on:submission-changed-topics="onQuestionSubmissionChangedTopics"
            />
          </td>
          <td id="creationDate">{{ item.question.creationDate }}</td>
        </tr>
      </template>
    </v-data-table>
    <edit-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="editQuestionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
      v-on:save-submission="onSaveQuestionSubmission"
      v-on:submit-submission="onSubmitQuestionSubmission"
    />
    <show-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="questionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
      v-on:close-show-question-dialog="onCloseShowQuestionSubmissionDialog"
    />
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on question's
      title to view submitted question and submission status.
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
import Review from '@/models/management/Review';

@Component({
  components: {
    'show-question-submission-dialog': ShowQuestionSubmissionDialog,
    'edit-question-submission-topics': EditQuestionSubmissionTopics,
    'edit-question-submission-dialog': EditQuestionSubmissionDialog,
  },
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
      this.headers.splice(3, 0, {
        text: 'Submitted by',
        value: 'name',
        align: 'center',
        width: '10%',
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
            RemoteServices.getTopics(),
          ])
        : await Promise.all([
            RemoteServices.getCourseExecutionQuestionSubmissions(),
            RemoteServices.getTopics(),
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
      JSON.stringify(question).toLowerCase().indexOf(search.toLowerCase()) !==
        -1
    );
  }

  newSubmission() {
    let question = new Question();
    question.status = 'SUBMITTED';
    this.currentQuestionSubmission = new QuestionSubmission();
    this.currentQuestionSubmission.prepareQuestionSubmission(
      this.$store.getters.getCurrentCourse.courseExecutionId,
      this.$store.getters.getUser.id,
      question
    );
    this.editQuestionSubmissionDialog = true;
  }

  editQuestionSubmission(submission: QuestionSubmission, e?: Event) {
    if (e) e.preventDefault();
    this.currentQuestionSubmission = submission;
    this.editQuestionSubmissionDialog = true;
  }

  onSaveQuestionSubmission(questionSubmission: QuestionSubmission) {
    this.questionSubmissions = this.questionSubmissions.filter(
      (qs) => qs.id !== questionSubmission.id
    );
    this.questionSubmissions.unshift(questionSubmission);
    this.editQuestionSubmissionDialog = false;
    this.currentQuestionSubmission = null;
  }

  async onSubmitQuestionSubmission(
    comment: string,
    questionSubmission: QuestionSubmission
  ) {
    this.onSaveQuestionSubmission(questionSubmission);
    await this.$store.dispatch('loading');
    try {
      let review = new Review();
      review.prepareReview(
        questionSubmission.id,
        'REQUEST_REVIEW',
        comment,
        this.$store.getters.getUser.id
      );
      await RemoteServices.createReview(review);
      await RemoteServices.toggleTeacherNotificationRead(
        questionSubmission.id,
        false
      );
      await this.getQuestionSubmissions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
    this.questionSubmissionDialog = true;

    try {
      if (this.$store.getters.isStudent) {
        await RemoteServices.toggleStudentNotificationRead(
          questionSubmission.id,
          true
        );
      } else if (this.$store.getters.isTeacher) {
        await RemoteServices.toggleTeacherNotificationRead(
          questionSubmission.id,
          true
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
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
          (questionSubmission) =>
            questionSubmission.question.id !=
            toDeleteQuestionSubmission.question.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  hasUnreadReviews(questionSubmission: QuestionSubmission) {
    return (
      (this.$store.getters.isStudent && !questionSubmission.studentRead) ||
      (this.$store.getters.isTeacher && !questionSubmission.teacherRead)
    );
  }
}
</script>

<style lang="scss">
.unread {
  background-color: rgba(51, 153, 255, 0.2);
}
.unread-icon {
  background-color: dodgerblue;
}
</style>
