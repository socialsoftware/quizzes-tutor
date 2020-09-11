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
    <v-data-iterator
      :items="userQuestionSubmissionsInfo"
      :search="search"
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:item="{ item }">
        <v-expansion-panels
          tile
          hover
          multiple
          focusable
          :readonly="item.hasNoSubmissions()"
        >
          <v-expansion-panel>
            <v-expansion-panel-header>
              {{ item.name }}
              <template v-slot:actions>
                <v-chip
                  v-for="status in item.numQuestionSubmissions"
                  :color="status.color"
                  :key="status.userId"
                >
                  {{ status.num }}
                </v-chip>
                <v-icon>$expand</v-icon>
                <v-chip> {{ item.totalQuestionSubmissions }} </v-chip>
              </template>
            </v-expansion-panel-header>
            <v-expansion-panel-content>
              <v-data-table
                :headers="headers"
                :items="item.questionSubmissions"
                :sort-by="['question.creationDate']"
                sort-desc
                :mobile-breakpoint="0"
                :items-per-page="5"
                :footer-props="{ itemsPerPageOptions: [5, 10, 15, 25] }"
              >
                <template v-slot:item.question.title="{ item }">
                  <div
                    @click="showQuestionSubmissionDialog(item)"
                    class="clickableTitle"
                  >
                    {{ item.question.title }}
                  </div>
                </template>
                <template v-slot:item.status="{ item }">
                  <v-chip :color="item.getStatusColor()" small>
                    <span>{{ item.getStatus() }}</span>
                  </v-chip>
                </template>
                <template v-slot:item.question.topics="{ item }">
                  <edit-question-submission-topics
                    :questionSubmission="item"
                    :topics="topics"
                    :readOnly="true"
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
                    <span>Show Question Submission</span>
                  </v-tooltip>
                </template>
              </v-data-table>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </template>
    </v-data-iterator>
    <show-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="questionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
    />
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on student to see their
      question submissions
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
    'edit-question-submission-topics': EditQuestionSubmissionTopics
  }
})
export default class SortQuestionSubmissionsByStudentView extends Vue {
  userQuestionSubmissionsInfo: UserQuestionSubmissionInfo[] = [];
  topics: Topic[] = [];
  currentQuestionSubmission: QuestionSubmission | null = null;
  questionSubmissionDialog: boolean = false;
  search: string = '';
  headers = QuestionSubmission.questionSubmissionHeader.slice();

  async created() {
    await this.getUserQuestionSubmissionsInfo();
  }

  async getUserQuestionSubmissionsInfo() {
    await this.$store.dispatch('loading');
    try {
      [this.userQuestionSubmissionsInfo, this.topics] = await Promise.all([
        RemoteServices.getAllStudentsSubmissionsInfo(),
        RemoteServices.getTopics()
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
