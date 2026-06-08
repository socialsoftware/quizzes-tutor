<template>
  <v-container fluid>
    <v-card class="table" outlined color="transparent">
      <v-row>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="queryQuestions"
            v-on:click="showQueryForm = !showQueryForm"
          >
            {{ !showQueryForm ? 'Open Query Form' : 'Close Query Form' }}
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="newQuestionButton"
            @click="newQuestion"
            >New Question
          </v-btn>
        </v-col>
        <v-col>
          <v-btn color="primary" dark @click="exportCourseQuestions"
            >Export All Course Questions
          </v-btn>
        </v-col>
        <v-col>
          <v-btn color="primary" dark @click="importCourseQuestions"
            >Import Questions to Course
          </v-btn>
        </v-col>
      </v-row>
    </v-card>
    <query-question-form
      v-show="showQueryForm"
      :availableOnly="false"
      class="table"
      v-on:query-questions="onQueryQuestions"
    />

    <v-card class="table">
      <v-data-table
        :custom-filter="customFilter"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        :headers="headers"
        :items="questions"
        :items-per-page="15"
        :mobile-breakpoint="0"
        :search="search"
        :sort-by="[{ key: 'creationDate', order: 'desc' }]"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              class="mx-2"
              label="Search"
            />
          </v-card-title>
        </template>

        <template v-slot:[`item.title`]="{ item }">
          <div
            class="clickableTitle"
            data-cy="questionTitleGrid"
            @click.stop="showQuestionDialog(item)"
            @contextmenu.stop="editQuestion(item, $event)"
          >
            {{ item.title }}
          </div>
        </template>

        <template v-slot:[`item.topics`]="{ item }">
          <edit-question-topics
            :question="item"
            :topics="topics"
            data-cy="Topics"
            v-on:question-changed-topics="onQuestionChangedTopics"
          />
        </template>

        <template v-slot:[`item.difficulty`]="{ item }">
          <v-chip
            v-if="item.difficulty"
            :color="getDifficultyColor(item.difficulty)"
            dark
            >{{ item.difficulty + '%' }}
          </v-chip>
        </template>

        <template v-slot:[`item.status`]="{ item }">
          <v-select
            v-model="item.status"
            :items="statusList"
            dense
            @update:model-value="setStatus(item.id as number, item.status)"
          >
            <template v-slot:selection="{ item: selectItem }">
              <v-chip :color="getStatusColor((selectItem as any).raw as string)" small>
                <span>{{ (selectItem as any).raw }}</span>
              </v-chip>
            </template>
          </v-select>
        </template>

        <template v-slot:[`item.image`]="{ item }">
          <v-file-input
            accept="image/*"
            dense
            show-size
            small-chips
            @change="handleFileUpload($event.target.files[0], item)"
          />
        </template>

        <template v-slot:[`item.action`]="{ item }">
          <div class="d-flex flex-column align-center" style="gap: 4px;">
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  class="action-button"
                  data-cy="showQuestionDialogButton"
                  @click.stop="showQuestionDialog(item)"
                  v-bind="props"
                  >visibility
                </v-icon>
              </template>
              <span>Show Question</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  class="action-button"
                  data-cy="showStudentViewDialogButton"
                  @click="showStudentViewDialog(item)"
                  v-bind="props"
                  >school
                </v-icon>
              </template>
              <span>Student View</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  class="action-button"
                  data-cy="duplicateQuestionButton"
                  @click="duplicateQuestion(item)"
                  v-bind="props"
                  >cached
                </v-icon>
              </template>
              <span>Duplicate Question</span>
            </v-tooltip>
            <v-tooltip v-if="item.numberOfAnswers === 0" bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  class="action-button"
                  data-cy="editQuestionButton"
                  @click="editQuestion(item)"
                  v-bind="props"
                  >edit
                </v-icon>
              </template>
              <span>Edit Question</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  class="action-button"
                  @click="showClarificationDialog(item)"
                  v-bind="props"
                  >fas fa-comments
                </v-icon>
              </template>
              <span>Show Clarifications</span>
            </v-tooltip>
            <v-tooltip v-if="item.numberOfAnswers === 0" bottom>
              <template v-slot:activator="{ props }">
                <v-icon
                  class="action-button"
                  color="red"
                  data-cy="deleteQuestionButton"
                  @click="deleteQuestion(item)"
                  v-bind="props"
                  >delete
                </v-icon>
              </template>
              <span>Delete Question</span>
            </v-tooltip>
          </div>
        </template>
      </v-data-table>
      <footer>
        <v-icon class="mr-2 action-button">mouse</v-icon>
        Left-click on question's title to view it.
        <v-icon class="mr-2 action-button">mouse</v-icon>
        Right-click on question's title to edit it.
      </footer>
      <upload-questions-dialog
        v-if="uploadQuestionsDialog"
        :dialog="uploadQuestionsDialog"
        @update:dialog="uploadQuestionsDialog = $event"
        @questions-uploaded="onQuestionsUploaded"
        @close-dialog="onCloseUploadQuestionsDialog"
      />
      <edit-question-dialog
        v-if="currentQuestion && editQuestionDialog"
        :dialog="editQuestionDialog"
        @update:dialog="editQuestionDialog = $event"
        :question="currentQuestion"
        @save-question="onSaveQuestion"
      />
      <show-question-dialog
        v-if="currentQuestion && questionDialog"
        v-model="questionDialog"
        :question="currentQuestion"
        @close-show-question-dialog="onCloseShowQuestionDialog"
      />
      <student-view-dialog
        v-if="statementQuestion && studentViewDialog"
        :dialog="studentViewDialog"
        @update:dialog="studentViewDialog = $event"
        :statementQuestion="statementQuestion"
        @close-show-question-dialog="onCloseStudentViewDialog"
      />
      <show-clarification-dialog
        v-if="currentQuestion && clarificationDialog"
        :dialog="clarificationDialog"
        @update:dialog="clarificationDialog = $event"
        :question="currentQuestion"
        @remove-clarification="onRemoveClarification"
        @close-show-clarification-dialog="onCloseShowClarificationDialog"
      />
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onErrorCaptured, nextTick } from 'vue';
import { useStore } from '@/store';
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

const store = useStore();

const questions = ref<Question[]>([]);

onErrorCaptured((err, instance, info) => {
  console.error('ERROR CAPTURED in QuestionsView:', err, info);
  return false; // don't propagate
});

const topics = ref<Topic[]>([]);
const currentQuestion = ref<Question | null>(null);
const statementQuestion = ref<StatementQuestion | null>(null);
const editQuestionDialog = ref(false);
const questionDialog = ref(false);
const studentViewDialog = ref(false);
const uploadQuestionsDialog = ref(false);
const clarificationDialog = ref(false);
const search = ref('');
const statusList = ref(['DISABLED', 'AVAILABLE', 'REMOVED']);
const showQueryForm = ref(true);

const headers = ref<any[]>([
  { title: 'Actions', key: 'action', align: 'start', width: '15%', sortable: false },
  { title: 'Title', key: 'title', align: 'start' },
  { title: 'Topics', key: 'topics', align: 'center', sortable: false },
  { title: 'Status', key: 'status', width: '150px', align: 'start' },
  { title: 'Image', key: 'image', align: 'center', sortable: false },
  { title: 'Clarifications', key: 'numberOfClarifications', align: 'center' },
  { title: 'Difficulty', key: 'difficulty', align: 'center' },
  { title: 'Answers', key: 'numberOfAnswers', align: 'center' },
  { title: 'Generated quizzes', key: 'numberOfGeneratedQuizzes', align: 'center' },
  { title: 'Non generated quizzes', key: 'numberOfNonGeneratedQuizzes', align: 'center' },
  { title: 'Creation Date', key: 'creationDate', width: '150px', align: 'center' },
]);

watch(editQuestionDialog, (newVal) => {
  if (!newVal) {
    currentQuestion.value = null;
  }
});

onMounted(async () => {
  store.setLoading();
  try {
    topics.value = await RemoteServices.getTopics();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const onQueryQuestions = (qs: Question[]) => {
  questions.value = qs;
};

const customFilter = (value: any, query: string, item?: any) => {
  return (
    query != null &&
    JSON.stringify(item?.raw || item).toLowerCase().indexOf(query.toLowerCase()) !== -1
  );
};

const onQuestionChangedTopics = (questionId: Number, changedTopics: Topic[]) => {
  let question = questions.value.find((question: Question) => question.id == questionId);
  if (question) {
    question.topics = changedTopics;
  }
};

const getDifficultyColor = (difficulty: number) => {
  if (difficulty < 25) return 'red';
  else if (difficulty < 50) return 'orange';
  else if (difficulty < 75) return 'lime';
  else return 'green';
};

const setStatus = async (questionId: number, status: string) => {
  try {
    await RemoteServices.setQuestionStatus(questionId, status);
    let question = questions.value.find((question) => question.id === questionId);
    if (question) {
      question.status = status;
    }
  } catch (error) {
    store.setError(error as string);
  }
};

const getStatusColor = (status: string) => {
  if (status === 'REMOVED') return 'red';
  else if (status === 'DISABLED') return 'orange';
  else return 'green';
};

const handleFileUpload = async (event: File, question: Question) => {
  if (question.id) {
    try {
      const imageURL = await RemoteServices.uploadImage(event, question.id);
      question.image = new Image();
      question.image.url = imageURL;
      confirm('Image ' + imageURL + ' was uploaded!');
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const showQuestionDialog = (question: Question) => {
  console.log('showQuestionDialog called, id:', question?.id, 'title:', question?.title, 'type:', question?.questionDetailsDto?.type);
  currentQuestion.value = question;
  questionDialog.value = true;
  console.log('showQuestionDialog SET - currentQuestion:', currentQuestion.value?.title, 'questionDialog:', questionDialog.value);
  nextTick(() => {
    console.log('showQuestionDialog NEXTTICK - currentQuestion:', currentQuestion.value?.title, 'questionDialog:', questionDialog.value);
  });
};

const showStudentViewDialog = async (question: Question) => {
  if (question.id) {
    try {
      statementQuestion.value = await RemoteServices.getStatementQuestion(question.id);
      studentViewDialog.value = true;
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const showClarificationDialog = (question: Question) => {
  currentQuestion.value = question;
  clarificationDialog.value = true;
};

const onCloseShowQuestionDialog = () => {
  currentQuestion.value = null;
  questionDialog.value = false;
};

const onCloseStudentViewDialog = () => {
  statementQuestion.value = null;
  studentViewDialog.value = false;
};

const onRemoveClarification = (questionId: number) => {
  let question = questions.value.find((question) => question.id === questionId);
  if (question) {
    question.numberOfClarifications--;
  }
};

const onCloseShowClarificationDialog = () => {
  currentQuestion.value = null;
  clarificationDialog.value = false;
};

const newQuestion = () => {
  currentQuestion.value = new Question();
  editQuestionDialog.value = true;
};

const editQuestion = (question: Question, e?: Event) => {
  if (e) e.preventDefault();
  currentQuestion.value = question;
  editQuestionDialog.value = true;
};

const duplicateQuestion = (question: Question) => {
  currentQuestion.value = new Question(question);
  currentQuestion.value.id = null;
  currentQuestion.value.questionDetailsDto.setAsNew();
  currentQuestion.value.image = null;
  editQuestionDialog.value = true;
};

const onSaveQuestion = async (question: Question) => {
  questions.value = questions.value.filter((q) => q.id !== question.id);
  questions.value.unshift(question);
  editQuestionDialog.value = false;
  currentQuestion.value = null;
};

const exportCourseQuestions = async () => {
  store.setLoading();
  let fileName = (store.getCurrentCourse?.name || 'Course') + '-Questions.zip';
  try {
    let result = await RemoteServices.exportCourseQuestions();
    const url = window.URL.createObjectURL(result as any);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const importCourseQuestions = () => {
  uploadQuestionsDialog.value = true;
};

const onQuestionsUploaded = (qs: Question[]) => {
  uploadQuestionsDialog.value = false;
  questions.value = [...qs, ...questions.value];
};

const onCloseUploadQuestionsDialog = () => {
  uploadQuestionsDialog.value = false;
};

const deleteQuestion = async (toDeletequestion: Question) => {
  if (
    toDeletequestion.id &&
    confirm('Are you sure you want to delete this question?')
  ) {
    try {
      await RemoteServices.deleteQuestion(toDeletequestion.id);
      questions.value = questions.value.filter(
        (question) => question.id != toDeletequestion.id
      );
    } catch (error) {
      store.setError(error as string);
    }
  }
};
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  :deep(.cm-editor),
  :deep(.cm-scroller) {
    min-height: 200px !important;
  }
}

.option-textarea {
  text-align: left;

  :deep(.cm-editor),
  :deep(.cm-scroller) {
    min-height: 100px !important;
  }
}
</style>
