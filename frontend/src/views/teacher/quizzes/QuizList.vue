<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="quizzes"
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

          <v-spacer />
          <v-btn color="primary" dark @click="$emit('newQuiz')">New Quiz</v-btn>
        </v-card-title>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="showQuizDialog(item.id)"
              >visibility</v-icon
            >
          </template>
          <span>Show Questions</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="showQuizAnswers(item.id)"
              >mdi-table</v-icon
            >
          </template>
          <span>View Results</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon large class="mr-2" v-on="on" @click="exportQuiz(item.id)"
              >fas fa-download</v-icon
            >
          </template>
          <span>Export</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.qrCodeOnly">
          <template v-slot:activator="{ on }">
            <v-icon large class="mr-2" v-on="on" @click="showQrCode(item.id)"
              >fas fa-qrcode</v-icon
            >
          </template>
          <span>Show QR Code</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.numberOfAnswers === 0">
          <template v-slot:activator="{ on }">
            <v-icon large class="mr-2" v-on="on" @click="editQuiz(item)"
              >edit</v-icon
            >
          </template>
          <span>Edit Quiz</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.numberOfAnswers === 0">
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="deleteQuiz(item.id)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Quiz</span>
        </v-tooltip>
      </template>

      <template v-slot:item.title="{ item }">
        <p
          @click="showQuizDialog(item.id)"
          @contextmenu="editQuiz(item, $event)"
          style="cursor: pointer"
        >
          {{ item.title }}
        </p>
      </template>

      <template v-slot:item.options="{ item }">
        <v-tooltip bottom v-if="item.timed">
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on">timer</v-icon>
          </template>
          <span>Displays a timer to conclusion and to show results</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.scramble">
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on">shuffle</v-icon>
          </template>
          <span>Question order is scrambled</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.oneWay">
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on">forward</v-icon>
          </template>
          <span>Students cannot go to previous question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on quiz's title to view it.
      <v-icon class="mr-2">mouse</v-icon>Right-click on quiz's title to edit it.
    </footer>

    <show-quiz-dialog v-if="quiz" v-model="quizDialog" :quiz="quiz" />

    <show-quiz-answers-dialog
      v-if="quizAnswers"
      v-model="quizAnswersDialog"
      :quiz-answers="quizAnswers"
      :correct-sequence="correctSequence"
      :timeToSubmission="timeToSubmission"
    />

    <v-dialog
      v-model="qrcodeDialog"
      @keydown.esc="qrcodeDialog = false"
      max-width="75%"
    >
      <v-card v-if="qrValue">
        <vue-qrcode
          class="qrcode"
          :value="qrValue.toString()"
          errorCorrectionLevel="M"
          :quality="1"
          :scale="100"
        />
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { Quiz } from '@/models/management/Quiz';
import RemoteServices from '@/services/RemoteServices';
import ShowQuizDialog from '@/views/teacher/quizzes/ShowQuizDialog.vue';
import ShowQuizAnswersDialog from '@/views/teacher/quizzes/ShowQuizAnswersDialog.vue';
import VueQrcode from 'vue-qrcode';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import { QuizAnswers } from '@/models/management/QuizAnswers';

@Component({
  components: {
    'show-quiz-answers-dialog': ShowQuizAnswersDialog,
    'show-quiz-dialog': ShowQuizDialog,
    'vue-qrcode': VueQrcode
  }
})
export default class QuizList extends Vue {
  @Prop({ type: Array, required: true }) readonly quizzes!: Quiz[];
  quiz: Quiz | null = null;
  quizAnswers: QuizAnswer[] = [];
  correctSequence: number[] = [];
  timeToSubmission: number = 0;
  search: string = '';

  quizDialog: boolean = false;
  quizAnswersDialog: boolean = false;
  qrcodeDialog: boolean = false;

  qrValue: number | null = null;
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '310px',
      sortable: false
    },
    { text: 'Title', value: 'title', align: 'left', width: '20%' },
    {
      text: 'Available Date',
      value: 'availableDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Conclusion Date',
      value: 'conclusionDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Results Date',
      value: 'resultsDate',
      align: 'center',
      width: '10%'
    },
    { text: 'Options', value: 'options', align: 'center', width: '150px' },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Answers',
      value: 'numberOfAnswers',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '10%'
    }
  ];

  async showQuizDialog(quizId: number) {
    try {
      this.quiz = await RemoteServices.getQuiz(quizId);
      this.quizDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async showQuizAnswers(quizId: number) {
    try {
      let quizAnswers: QuizAnswers = await RemoteServices.getQuizAnswers(
        quizId
      );

      this.quizAnswers = quizAnswers.quizAnswers;
      this.correctSequence = quizAnswers.correctSequence;
      this.timeToSubmission = quizAnswers.timeToSubmission;
      this.quizAnswersDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  editQuiz(quiz: Quiz, e?: Event) {
    if (e) e.preventDefault();
    this.$emit('editQuiz', quiz.id);
  }

  showQrCode(quizId: number) {
    this.qrValue = quizId;
    this.qrcodeDialog = true;
  }

  async exportQuiz(quizId: number) {
    let fileName =
      this.quizzes.filter(quiz => quiz.id == quizId)[0].title + '.zip';
    try {
      let result = await RemoteServices.exportQuiz(quizId);
      const url = window.URL.createObjectURL(result);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async deleteQuiz(quizId: number) {
    if (confirm('Are you sure you want to delete this quiz?')) {
      try {
        await RemoteServices.deleteQuiz(quizId);
        this.$emit('deleteQuiz', quizId);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss">
.qrcode {
  width: 80vw !important;
  height: 80vw !important;
  max-width: 80vh !important;
  max-height: 80vh !important;
}
</style>
