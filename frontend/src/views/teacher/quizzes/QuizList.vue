<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="quizzes"
      :search="search"
      multi-sort
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
              small
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
              small
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
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="$emit('editQuiz', item.id)"
              >edit</v-icon
            >
          </template>
          <span>Edit Quiz</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteQuiz(item.id)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Quiz</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="showQrCode(item.id)"
              >fas fa-qrcode</v-icon
            >
          </template>
          <span>Show QR Code</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <show-quiz-dialog v-if="quiz" v-model="quizDialog" :quiz="quiz" />

    <show-quiz-answers-dialog
      v-if="quizAnswers"
      v-model="quizAnswersDialog"
      :quiz-answers="quizAnswers"
      :correct-sequence="correctSequence"
      :secondsToSubmission="secondsToSubmission"
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
  secondsToSubmission: number = 0;
  search: string = '';

  quizDialog: boolean = false;
  quizAnswersDialog: boolean = false;
  qrcodeDialog: boolean = false;

  qrValue: number | null = null;
  headers: object = [
    { text: 'Title', value: 'title', align: 'left', width: '30%' },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '10%'
    },
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
    { text: 'Scramble', value: 'scramble', align: 'center', width: '10%' },
    { text: 'QRCode Only', value: 'qrCodeOnly', align: 'center', width: '10%' },
    {
      text: 'One Way Quiz',
      value: 'oneWay',
      align: 'center',
      width: '10%'
    },
    { text: 'Type', value: 'type', align: 'center', width: '10%' },
    { text: 'Series', value: 'series', align: 'center', width: '5%' },
    { text: 'Version', value: 'version', align: 'center', width: '5%' },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Timer to submission',
      value: 'timerToSubmission',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Answers',
      value: 'numberOfAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '1%',
      sortable: false
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
      this.secondsToSubmission = quizAnswers.secondsToSubmission;
      this.quizAnswersDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  showQrCode(quizId: number) {
    this.qrValue = quizId;
    this.qrcodeDialog = true;
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
