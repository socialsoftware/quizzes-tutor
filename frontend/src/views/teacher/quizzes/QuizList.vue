<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="quizzes"
      :search="search"
      :sort-by="[{ key: 'creationDate', order: 'desc' }]"
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

          <v-spacer />
          <v-btn
            color="primary"
            dark
            data-cy="newQuizButton"
            @click="$emit('newQuiz')"
            >New Quiz</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:[`item.action`]="{ item }">
        <div class="quiz-actions-grid">
          <v-tooltip bottom>
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="openQuizDialog(item.id)">visibility</v-icon>
            </template>
            <span>Show Questions</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="duplicateQuiz(item.id)">cached</v-icon>
            </template>
            <span>Duplicate Quiz</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="showQuizAnswers(item)">mdi-table</v-icon>
            </template>
            <span>View Results</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="exportQuiz(item.id)">fas fa-download</v-icon>
            </template>
            <span>Export</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.numberOfAnswers === 0">
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="editQuiz(item)">edit</v-icon>
            </template>
            <span>Edit Quiz</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="populateWithQuizAnswers(item.id)">people</v-icon>
            </template>
            <span>Populate with answers</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="removeNonAnsweredQuizAnswers(item.id)">people_outline</v-icon>
            </template>
            <span>Remove non answered</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.qrCodeOnly">
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="showQrCode(item.id)">fas fa-qrcode</v-icon>
            </template>
            <span>Show QR Code</span>
          </v-tooltip>
          <v-tooltip bottom v-if="isFraudServiceAvailableToQuiz(item)">
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" @click="showQuizFraudScores(item)">mdi-account-alert</v-icon>
            </template>
            <span>View Fraud Scores</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.numberOfAnswers === 0">
            <template v-slot:activator="{ props }">
              <v-icon class="action-button" v-bind="props" data-cy="deleteQuizButton" @click="deleteQuiz(item.id)" color="red">delete</v-icon>
            </template>
            <span>Delete Quiz</span>
          </v-tooltip>
        </div>
      </template>

      <template v-slot:[`item.title`]="{ item }">
        <div
          @click="openQuizDialog(item.id)"
          @contextmenu="editQuiz(item, $event)"
          class="clickableTitle"
        >
          {{ item.title }}
        </div>
      </template>
      <template v-slot:[`item.code`]="{ item }">
        <div v-if="item.qrCodeOnly">
          {{ item.code }}
        </div>
      </template>

      <template v-slot:[`item.options`]="{ item }">
        <v-tooltip bottom v-if="item.timed">
          <template v-slot:activator="{ props }">
            <v-icon class="mr-2 action-button" v-bind="props">timer</v-icon>
          </template>
          <span>Displays a timer to conclusion and to show results</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.scramble">
          <template v-slot:activator="{ props }">
            <v-icon class="mr-2 action-button" v-bind="props">shuffle</v-icon>
          </template>
          <span>Question order is scrambled</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.oneWay">
          <template v-slot:activator="{ props }">
            <v-icon class="mr-2 action-button" v-bind="props">forward</v-icon>
          </template>
          <span>Students cannot go to previous question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Left-click on quiz's
      title to view it.
      <v-icon class="mr-2 action-button">mouse</v-icon>Right-click on quiz's
      title to edit it.
    </footer>

    <show-quiz-dialog v-if="quiz" v-model:dialog="quizDialog" :quiz="quiz" />

    <show-quiz-answers-dialog
      v-if="quizAnswers && quiz"
      v-model:dialog="quizAnswersDialog"
      :conclusion-date="quiz.conclusionDate"
      :quizAnswers="quizAnswers"
    />

    <v-dialog
      v-model="qrcodeDialog"
      @keydown.esc="qrcodeDialog = false"
      max-width="75%"
    >
      <v-card v-if="qrValue">
        <qrcode-vue
          class="qrcode"
          :value="qrValue.toString()"
          level="M"
          :size="800"
        />
      </v-card>
    </v-dialog>

    <show-quiz-fraud-scores-dialog
      v-if="quiz && quizFraudInformation"
      v-model:dialog="quizFraudScoresDialog"
      :quiz="quiz"
      :quizFraudInformation="quizFraudInformation"
    /> 
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useStore } from '@/store';
import { Quiz } from '@/models/management/Quiz';
import RemoteServices from '@/services/RemoteServices';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import ShowQuizAnswersDialog from '@/views/teacher/quizzes/ShowQuizAnswersDialog.vue';
import QrcodeVue from 'qrcode.vue';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import { QuizFraudInformation } from '@/models/management/fraud/QuizFraudInformation';
import ShowQuizFraudScoresDialog from '@/views/teacher/fraud/ShowQuizFraudScoresDialog.vue';

const props = defineProps<{
  quizzes: Quiz[];
}>();

const emit = defineEmits(['duplicateQuiz', 'editQuiz', 'deleteQuiz', 'updateQuiz', 'newQuiz']);
const store = useStore();

const quiz = ref<Quiz | null>(null);
const quizAnswers = ref<QuizAnswers | null>(null);
const quizFraudInformation = ref<QuizFraudInformation | null>(null);
const correctSequence = ref<number[]>([]);
const timeToSubmission = ref<number>(0);
const search = ref<string>('');

const quizDialog = ref(false);
const quizAnswersDialog = ref(false);
const qrcodeDialog = ref(false);
const quizFraudScoresDialog = ref(false);

const qrValue = ref<number | null>(null);
const headers = ref<any[]>([
  { title: 'Actions', key: 'action', align: 'start', width: '75px', sortable: false },
  { title: 'Title', key: 'title', align: 'start', width: '30%' },
  { title: 'Code', key: 'code', align: 'center', width: '150px' },
  { title: 'Available Date', key: 'availableDate', align: 'center', width: '150px' },
  { title: 'Conclusion Date', key: 'conclusionDate', align: 'center', width: '150px' },
  { title: 'Results Date', key: 'resultsDate', align: 'center', width: '150px' },
  { title: 'Options', key: 'options', align: 'center', width: '150px', sortable: false },
  { title: 'Questions', key: 'numberOfQuestions', width: '100px', align: 'center' },
  { title: 'Answers', key: 'numberOfAnswers', width: '100px', align: 'center' },
  { title: 'Creation Date', key: 'creationDate', width: '150px', align: 'center' },
]);

const openQuizDialog = async (quizId: number) => {
  try {
    quiz.value = await RemoteServices.getQuiz(quizId);
    quizDialog.value = true;
  } catch (error) {
    store.setError(error as string);
  }
};

const duplicateQuiz = async (quizId: number) => {
  store.setLoading();
  try {
    let duplicate = await RemoteServices.duplicateQuiz(quizId);
    emit('duplicateQuiz', duplicate);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const showQuizAnswers = async (q: Quiz) => {
  store.setLoading();
  try {
    quizAnswers.value = await RemoteServices.getQuizAnswers(q.id);
    quiz.value = q;
    quizAnswersDialog.value = true;
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const showQuizFraudScores = async (q: Quiz) => {
  store.setLoading();
  try {
    quizFraudInformation.value = await RemoteServices.getQuizFraudInformation(q.id);
    quiz.value = q;
    quizFraudScoresDialog.value = true;
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const editQuiz = (q: Quiz, e?: Event) => {
  if (e) e.preventDefault();
  emit('editQuiz', q.id);
};

const showQrCode = (quizId: number) => {
  qrValue.value = quizId;
  qrcodeDialog.value = true;
};

const exportQuiz = async (quizId: number) => {
  store.setLoading();
  let fileName = props.quizzes.filter((q) => q.id == quizId)[0].title + '.tar.gz';
  try {
    let result = await RemoteServices.exportQuiz(quizId);
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

const deleteQuiz = async (quizId: number) => {
  if (confirm('Are you sure you want to delete this quiz?')) {
    store.setLoading();
    try {
      await RemoteServices.deleteQuiz(quizId);
      emit('deleteQuiz', quizId);
    } catch (error) {
      store.setError(error as string);
    }
    store.clearLoading();
  }
};

const populateWithQuizAnswers = async (quizId: number) => {
  store.setLoading();
  try {
    let q: Quiz = await RemoteServices.populateWithQuizAnswers(quizId);
    emit('updateQuiz', q);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const removeNonAnsweredQuizAnswers = async (quizId: number) => {
  store.setLoading();
  try {
    let q: Quiz = await RemoteServices.removeNonAnsweredQuizAnswers(quizId);
    emit('updateQuiz', q);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const isFraudServiceAvailableToQuiz = (q: Quiz) => {
  return (
    q.timed &&
    q.oneWay &&
    new Date(q.conclusionDate) < new Date() &&
    q.numberOfAnswers > 0 &&
    q.numberOfQuestions == 5
  );
};
</script>

<style lang="scss">
.qrcode {
  width: 80vw !important;
  height: 80vw !important;
  max-width: 80vh !important;
  max-height: 80vh !important;
}

// Grid de 2 ícones por linha na coluna Actions dos quizzes
.quiz-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 32px);
  gap: 4px;
  align-items: center;
  justify-items: center;
  padding: 4px 0;
}
</style>
