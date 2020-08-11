<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questionSubmissions"
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
          @click="showQuestionSubmissionDialog(item)"
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
        <edit-question-submission-topics
          v-if="item.question.status !== 'REJECTED'"
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
              >visibility</v-icon
            >
          </template>
          <span>Show Question Submission</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="questionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
    />
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on question's title to view
      submitted question and submission status.
    </footer>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import ShowQuestionSubmissionDialog from '@/views/teacher/question-submissions/ShowQuestionSubmissionDialog.vue';
import EditQuestionSubmissionTopics from '@/views/teacher/question-submissions/EditQuestionSubmissionTopics.vue';
import ViewQuestionSubmissionTopics from '@/views/teacher/question-submissions/ViewQuestionSubmissionTopics.vue';

@Component({
  components: {
    'show-question-submission-dialog': ShowQuestionSubmissionDialog,
    'edit-question-submission-topics': EditQuestionSubmissionTopics,
    'view-question-submission-topics': ViewQuestionSubmissionTopics
  }
})
export default class QuestionSubmissionView extends Vue {
  questionSubmissions: QuestionSubmission[] = [];
  topics: Topic[] = [];
  currentQuestionSubmission: QuestionSubmission | null = null;
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
      [this.questionSubmissions, this.topics] = await Promise.all([
        RemoteServices.getCourseExecutionQuestionSubmissions(),
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
    await this.$store.dispatch('loading');
    try {
      this.currentQuestionSubmission = questionSubmission;
      if (this.isReviewable(this.currentQuestionSubmission)) {
        try {
          await RemoteServices.toggleInReviewStatus(
            questionSubmission!.id!,
            true
          );
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
      this.questionSubmissionDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('questionSubmissionDialog')
  async onCloseShowQuestionSubmissionDialog() {
    if (!this.questionSubmissionDialog) {
      await this.$store.dispatch('loading');
      try {
        [this.questionSubmissions] = await Promise.all([
          RemoteServices.getCourseExecutionQuestionSubmissions()
        ]);
        this.questionSubmissionDialog = false;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
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
        let questionId = toDeleteQuestionSubmission.question.id;
        if (questionId != null) await RemoteServices.deleteQuestion(questionId);
        this.questionSubmissions = this.questionSubmissions.filter(
          questionSubmission => questionSubmission.question.id != questionId
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  isReviewable(questionSubmission: QuestionSubmission) {
    return (
      questionSubmission.question.status == 'IN_REVISION' ||
      questionSubmission.question.status == 'IN_REVIEW'
    );
  }
}
</script>

<style lang="scss" scoped></style>
