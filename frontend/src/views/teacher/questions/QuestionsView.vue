<template>
  <v-container fluid>
    <v-card class="table">
      <v-row>
        <v-col>
          <v-btn
            color="primary"
            dark
            v-on:click="showQueryForm = !showQueryForm"
            data-cy="queryQuestions"
          >
            {{ !showQueryForm ? 'Open Query Form' : 'Close Query Form' }}</v-btn
          >
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            @click="newQuestion"
            data-cy="newQuestionButton"
            >New Question</v-btn
          ></v-col
        >
        <v-col>
          <v-btn color="primary" dark @click="exportCourseQuestions"
            >Export All Course Questions</v-btn
          ></v-col
        >
        <v-col>
          <v-btn color="primary" dark @click="importCourseQuestions"
            >Import Questions to Course</v-btn
          >
        </v-col>
      </v-row>
    </v-card>
    <query-question-form
      class="table"
      v-show="showQueryForm"
      :availableOnly="false"
      v-on:query-questions="onQueryQuestions"
    />

    <v-card class="table">
      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :items="questions"
        :search="search"
        :sort-by="['creationDate']"
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
              class="mx-2"
            />
          </v-card-title>
        </template>

        <template v-slot:[`item.title`]="{ item }">
          <div
            @click="showQuestionDialog(item)"
            @contextmenu="editQuestion(item, $event)"
            class="clickableTitle"
            data-cy="questionTitleGrid"
          >
            {{ item.title }}
          </div>
        </template>

        <template v-slot:[`item.topics`]="{ item }">
          <edit-question-topics
            :question="item"
            :topics="topics"
            v-on:question-changed-topics="onQuestionChangedTopics"
          />
        </template>

        <template v-slot:[`item.difficulty`]="{ item }">
          <v-chip
            v-if="item.difficulty"
            :color="getDifficultyColor(item.difficulty)"
            dark
            >{{ item.difficulty + '%' }}</v-chip
          >
        </template>

        <template v-slot:[`item.status`]="{ item }">
          <v-select
            v-model="item.status"
            :items="statusList"
            dense
            @change="setStatus(item.id, item.status)"
          >
            <template v-slot:selection="{ item }">
              <v-chip :color="getStatusColor(item)" small>
                <span>{{ item }}</span>
              </v-chip>
            </template>
          </v-select>
        </template>

        <template v-slot:[`item.image`]="{ item }">
          <v-file-input
            show-size
            dense
            small-chips
            @change="handleFileUpload($event, item)"
            accept="image/*"
          />
        </template>

        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="showQuestionDialog(item)"
                >visibility</v-icon
              >
            </template>
            <span>Show Question</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="showStudentViewDialog(item)"
                >school</v-icon
              >
            </template>
            <span>Student View</span>
          </v-tooltip>
          <v-tooltip bottom data-cy="duplicateButton">
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="duplicateQuestion(item)"
                >cached</v-icon
              >
            </template>
            <span>Duplicate Question</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.numberOfAnswers === 0">
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="editQuestion(item)"
                >edit</v-icon
              >
            </template>
            <span>Edit Question</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                @click="showClarificationDialog(item)"
                >fas fa-comments</v-icon
              >
            </template>
            <span>Show Clarifications</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.numberOfAnswers === 0">
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                v-on="on"
                data-cy="deleteQuestionButton"
                @click="deleteQuestion(item)"
                color="red"
                >delete</v-icon
              >
            </template>
            <span>Delete Question</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <footer>
        <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on
        question's title to view it.
        <v-icon class="mr-2 action-button">mouse</v-icon>Right-click on
        question's title to edit it.
      </footer>
      <upload-questions-dialog
        v-if="uploadQuestionsDialog"
        v-model="uploadQuestionsDialog"
        v-on:questions-uploaded="onQuestionsUploaded"
        v-on:close-dialog="onCloseUploadQuestionsDialog"
      />
      <edit-question-dialog
        v-if="currentQuestion && editQuestionDialog"
        v-model="editQuestionDialog"
        :question="currentQuestion"
        v-on:save-question="onSaveQuestion"
      />
      <show-question-dialog
        v-if="currentQuestion && questionDialog"
        v-model="questionDialog"
        :question="currentQuestion"
        v-on:close-show-question-dialog="onCloseShowQuestionDialog"
      />
      <student-view-dialog
        v-if="statementQuestion && studentViewDialog"
        v-model="studentViewDialog"
        :statementQuestion="statementQuestion"
        v-on:close-show-question-dialog="onCloseStudentViewDialog"
      />
      <show-clarification-dialog
        v-if="currentQuestion && clarificationDialog"
        v-model="clarificationDialog"
        :question="currentQuestion"
        v-on:remove-clarification="onRemoveClarification"
        v-on:close-show-clarification-dialog="onCloseShowClarificationDialog"
      />
    </v-card>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';
import ShowClarificationDialog from '../discussions/ShowClarificationDialog.vue';
import UploadQuestionsDialog from '@/views/teacher/questions/UploadQuestionsDialog.vue';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StudentViewDialog from '@/views/teacher/questions/StudentViewDialog.vue';
import QueryQuestionForm from '@/views/teacher/questions/QueryQuestionForm.vue';

@Component({
  components: {
    'query-question-form': QueryQuestionForm,
    'upload-questions-dialog': UploadQuestionsDialog,
    'show-question-dialog': ShowQuestionDialog,
    'student-view-dialog': StudentViewDialog,
    'show-clarification-dialog': ShowClarificationDialog,
    'edit-question-dialog': EditQuestionDialog,
    'edit-question-topics': EditQuestionTopics,
  },
})
export default class QuestionsView extends Vue {
  questions: Question[] = [];
  topics: Topic[] = [];
  currentQuestion: Question | null = null;
  statementQuestion: StatementQuestion | null = null;
  editQuestionDialog: boolean = false;
  questionDialog: boolean = false;
  studentViewDialog: boolean = false;
  uploadQuestionsDialog: boolean = false;
  clarificationDialog: boolean = false;
  search: string = '';
  statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
  showQueryForm: boolean = true;

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    { text: 'Title', value: 'title', width: '50%', align: 'left' },
    {
      text: 'Topics',
      value: 'topics',
      width: '30%',
      align: 'center',
      sortable: false,
    },
    { text: 'Status', value: 'status', width: '150px', align: 'left' },
    {
      text: 'Image',
      value: 'image',
      width: '10%',
      align: 'center',
      sortable: false,
    },
    {
      text: 'Clarifications',
      value: 'numberOfClarifications',
      width: '5px',
      align: 'center',
    },
    { text: 'Difficulty', value: 'difficulty', width: '5px', align: 'center' },
    {
      text: 'Answers',
      value: 'numberOfAnswers',
      width: '5px',
      align: 'center',
    },
    {
      text: 'Generated quizzes',
      value: 'numberOfGeneratedQuizzes',
      width: '5px',
      align: 'center',
    },
    {
      text: 'Non generated quizzes',
      value: 'numberOfNonGeneratedQuizzes',
      width: '5px',
      align: 'center',
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      width: '150px',
      align: 'center',
    },
  ];

  @Watch('editQuestionDialog')
  closeError() {
    if (!this.editQuestionDialog) {
      this.currentQuestion = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onQueryQuestions(questions: Question[]) {
    this.questions = questions;
  }

  customFilter(value: string, search: string, question: Question) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(question).toLowerCase().indexOf(search.toLowerCase()) !==
        -1
    );
  }

  onQuestionChangedTopics(questionId: Number, changedTopics: Topic[]) {
    let question = this.questions.find(
      (question: Question) => question.id == questionId
    );
    if (question) {
      question.topics = changedTopics;
    }
  }

  getDifficultyColor(difficulty: number) {
    if (difficulty < 25) return 'red';
    else if (difficulty < 50) return 'orange';
    else if (difficulty < 75) return 'lime';
    else return 'green';
  }

  async setStatus(questionId: number, status: string) {
    try {
      await RemoteServices.setQuestionStatus(questionId, status);
      let question = this.questions.find(
        (question) => question.id === questionId
      );
      if (question) {
        question.status = status;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getStatusColor(status: string) {
    if (status === 'REMOVED') return 'red';
    else if (status === 'DISABLED') return 'orange';
    else return 'green';
  }

  async handleFileUpload(event: File, question: Question) {
    if (question.id) {
      try {
        const imageURL = await RemoteServices.uploadImage(event, question.id);
        question.image = new Image();
        question.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  async showStudentViewDialog(question: Question) {
    if (question.id) {
      try {
        this.statementQuestion = await RemoteServices.getStatementQuestion(
          question.id
        );
        this.studentViewDialog = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  showClarificationDialog(question: Question) {
    this.currentQuestion = question;
    this.clarificationDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.currentQuestion = null;
    this.questionDialog = false;
  }

  onCloseStudentViewDialog() {
    this.statementQuestion = null;
    this.studentViewDialog = false;
  }

  onRemoveClarification(questionId: number) {
    let question = this.questions.find(
      (question) => question.id === questionId
    );
    if (question) {
      question.numberOfClarifications--;
    }
  }

  onCloseShowClarificationDialog() {
    this.currentQuestion = null;
    this.clarificationDialog = false;
  }

  newQuestion() {
    this.currentQuestion = new Question();
    this.editQuestionDialog = true;
  }

  editQuestion(question: Question, e?: Event) {
    if (e) e.preventDefault();
    this.currentQuestion = question;
    this.editQuestionDialog = true;
  }

  duplicateQuestion(question: Question) {
    this.currentQuestion = new Question(question);
    this.currentQuestion.id = null;
    this.currentQuestion.questionDetailsDto.setAsNew();
    this.currentQuestion.image = null;
    this.editQuestionDialog = true;
  }

  async onSaveQuestion(question: Question) {
    this.questions = this.questions.filter((q) => q.id !== question.id);
    this.questions.unshift(question);
    this.editQuestionDialog = false;
    this.currentQuestion = null;
  }

  async exportCourseQuestions() {
    await this.$store.dispatch('loading');
    let fileName = this.$store.getters.getCurrentCourse.name + '-Questions.zip';
    try {
      let result = await RemoteServices.exportCourseQuestions();
      const url = window.URL.createObjectURL(result);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  importCourseQuestions() {
    this.uploadQuestionsDialog = true;
  }

  onQuestionsUploaded(questions: Question[]) {
    this.uploadQuestionsDialog = false;
    this.questions = [...questions, ...this.questions];
  }

  onCloseUploadQuestionsDialog() {
    this.uploadQuestionsDialog = false;
  }

  async deleteQuestion(toDeletequestion: Question) {
    if (
      toDeletequestion.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteQuestion(toDeletequestion.id);
        this.questions = this.questions.filter(
          (question) => question.id != toDeletequestion.id
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
