import QrcodeVue from "*.vue";
<template>
  <div class="container">
    <qrcode-stream v-if="!quizId" @decode="onDecode"></qrcode-stream>
    <div v-else>Wait for {{ seconds }} seconds</div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream } from 'vue-qrcode-reader';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';

@Component({
  components: {
    'qrcode-stream': QrcodeStream
  }
})
export default class ScanView extends Vue {
  quizId: number | null = null;
  seconds: number = 0;

  async onDecode(decodedString: String) {
    this.quizId = Number(decodedString);
    this.getEvaluationQuiz();
  }

  countDownTimer() {
    if (this.seconds > -1) {
      setTimeout(() => {
        this.seconds -= 1;
        this.countDownTimer();
      }, 1000);
    } else {
      this.getEvaluationQuiz();
    }
  }

  async getEvaluationQuiz() {
    if (this.quizId) {
      try {
        let quiz: StatementQuiz = await RemoteServices.getEvaluationQuiz(
          this.quizId
        );
        if (quiz.secondsToAvailability) {
          this.seconds = quiz.secondsToAvailability;
          this.countDownTimer();
        } else {
          this.goToSolveQuiz(quiz);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async goToSolveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    statementManager.answers = quiz.questions.map(
      (question: StatementQuestion) => {
        let answer = new StatementAnswer();
        answer.quizQuestionId = question.quizQuestionId;
        return answer;
      }
    );
    await this.$router.push({ name: 'solve-quiz' });
  }
}
</script>

<style lang="scss" scoped></style>
