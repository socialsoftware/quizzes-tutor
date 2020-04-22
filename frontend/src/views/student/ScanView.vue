import QrcodeVue from "*.vue";
<template>
  <div class="container">
    <qrcode-stream v-if="!quizId" @decode="onDecode"></qrcode-stream>
    <v-card v-else>
      <v-card-title>
        Hold on and wait for {{ timeToRequest / 1000 }} seconds to start the
        quiz!
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream } from 'vue-qrcode-reader';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';

@Component({
  components: {
    'qrcode-stream': QrcodeStream
  }
})
export default class ScanView extends Vue {
  quizId: number | null = null;
  timeToRequest: number = 0;
  timerId!: number;
  startDate!: number;

  async onDecode(decodedString: String) {
    this.quizId = Number(decodedString);
    this.getQuizByQRCode();
  }

  async getQuizByQRCode() {
    if (this.quizId) {
      try {
        let quiz: StatementQuiz = await RemoteServices.getQuizByQRCode(
          this.quizId
        );
        if (quiz.timeToAvailability) {
          this.timeToRequest = quiz.timeToAvailability;
          this.startDate = Date.now();
          this.timerId = setInterval(this.updateTimer, 1000);
        } else {
          this.goToSolveQuiz(quiz);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
  }

  updateTimer() {
    if (this.timeToRequest - Math.floor(Date.now() - this.startDate) >= 0) {
      if (this.$router.currentRoute.name !== 'scan') {
        clearInterval(this.timerId);
      }
    } else {
      clearInterval(this.timerId);
      this.getQuizByQRCode();
    }
  }

  async goToSolveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    await this.$router.push({ name: 'solve-quiz' });
  }
}
</script>

<style lang="scss" scoped></style>
