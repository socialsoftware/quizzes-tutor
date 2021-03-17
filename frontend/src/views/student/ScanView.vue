<template>
  <div class="container">
    <qrcode-stream v-if="!quizId" @decode="onDecode"></qrcode-stream>
    <v-card v-else>
      <v-card-title class="justify-center">
        Hold on and wait {{ timer }} to start the quiz!
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream } from 'vue-qrcode-reader';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component({
  components: {
    'qrcode-stream': QrcodeStream,
  },
})
export default class ScanView extends Vue {
  quizId: number | null = null;
  quiz: StatementQuiz | null = null;
  timer: string = '';

  async onDecode(decodedString: String) {
    this.quizId = Number(decodedString);
    this.getQuizByQRCode();
  }

  async getQuizByQRCode() {
    if (this.quizId && this.$router.currentRoute.name === 'scan') {
      try {
        this.quiz = await RemoteServices.getQuizByQRCode(this.quizId);

        if (!this.quiz.timeToAvailability) {
          let statementManager: StatementManager = StatementManager.getInstance;
          statementManager.statementQuiz = this.quiz;
          await this.$router.push({ name: 'solve-quiz' });
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
  }

  @Watch('quiz.timeToAvailability')
  timerMethod() {
    if (!!this.quiz && !this.quiz.timeToAvailability) {
      this.getQuizByQRCode();
    }

    this.timer = milisecondsToHHMMSS(this.quiz?.timeToAvailability);
  }
}
</script>

<style lang="scss" scoped></style>
