<template>
  <div style="height: 100%">
    <div
      tabindex="0"
      class="quiz-container"
      @keydown.left="decreaseOrder"
      @keydown.right="increaseOrder"
      v-if="!confirmed"
    >
      <header>
        <span
          class="timer"
          @click="hideTime = !hideTime"
          v-if="secondsToSubmission > 0"
        >
          <i class="fas fa-clock"></i>
          <span v-if="!hideTime">{{ HHMMSS }}</span>
        </span>
        <span class="end-quiz" @click="confirmationDialog = true"
          ><i class="fas fa-times" />End Quiz</span
        >
      </header>

      <div class="question-navigation">
        <div class="navigation-buttons">
          <span
            v-for="index in +statementQuiz.questions.length"
            v-bind:class="[
              'question-button',
              index === questionOrder + 1 ? 'current-question-button' : ''
            ]"
            :key="index"
            @click="changeOrder(index - 1)"
          >
            {{ index }}
          </span>
        </div>
        <span
          class="left-button"
          @click="decreaseOrder"
          v-if="questionOrder !== 0"
          ><i class="fas fa-chevron-left"
        /></span>
        <span
          class="right-button"
          @click="increaseOrder"
          v-if="questionOrder !== statementQuiz.questions.length - 1"
          ><i class="fas fa-chevron-right"
        /></span>
      </div>
      <question-component
        v-model="questionOrder"
        v-if="statementQuiz.answers[questionOrder]"
        :optionId="statementQuiz.answers[questionOrder].optionId"
        :question="statementQuiz.questions[questionOrder]"
        :questionNumber="statementQuiz.questions.length"
        @increase-order="increaseOrder"
        @select-option="changeAnswer"
        @decrease-order="decreaseOrder"
      />

      <v-dialog v-model="confirmationDialog" width="50%">
        <v-card>
          <v-card-title primary-title class="secondary white--text headline">
            Confirmation
          </v-card-title>

          <v-card-text class="text--black title">
            <br />
            Are you sure you want to finish?
            <br />
            <span
              v-if="
                statementQuiz.answers
                  .map(answer => answer.optionId)
                  .filter(optionId => optionId == null).length
              "
            >
              You still have
              {{
                statementQuiz.answers
                  .map(answer => answer.optionId)
                  .filter(optionId => optionId == null).length
              }}
              unanswered questions!
            </span>
          </v-card-text>

          <v-divider />

          <v-card-actions>
            <v-spacer />
            <v-btn color="secondary" text @click="confirmationDialog = false">
              Cancel
            </v-btn>
            <v-btn color="primary" text @click="endQuiz">
              I'm sure
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </div>

    <v-card v-else-if="secondsToSubmission">
      <v-card-title>
        Hold on and wait for {{ secondsToSubmission + 1 }} seconds to view the
        results
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import QuestionComponent from '@/views/student/quiz/QuizComponent.vue';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';

@Component({
  components: {
    'question-component': QuestionComponent
  }
})
export default class QuizView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  statementQuiz: StatementQuiz | null =
    StatementManager.getInstance.statementQuiz;
  confirmationDialog: boolean = false;
  confirmed: boolean = false;
  startTime: Date = new Date();
  questionOrder: number = 0;
  secondsToSubmission: number =
    StatementManager.getInstance.statementQuiz?.secondsToSubmission ?? 0;
  hideTime: boolean = false;

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    }
    this.countDownToResults();
  }

  increaseOrder(): void {
    if (this.questionOrder + 1 < +this.statementQuiz!.questions.length) {
      this.calculateTime();
      this.questionOrder += 1;
    }
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.calculateTime();
      this.questionOrder -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementQuiz!.questions.length) {
      this.calculateTime();
      this.questionOrder = n;
    }
  }

  async changeAnswer(optionId: number) {
    if (this.statementQuiz && this.statementQuiz.answers[this.questionOrder]) {
      try {
        this.calculateTime();

        await RemoteServices.submitAnswer(
          this.statementQuiz.id,
          this.statementQuiz.answers[this.questionOrder]
        );

        if (
          this.statementQuiz.answers[this.questionOrder].optionId === optionId
        ) {
          this.statementQuiz.answers[this.questionOrder].optionId = null;
        } else {
          this.statementQuiz.answers[this.questionOrder].optionId = optionId;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async endQuiz() {
    await this.$store.dispatch('loading');
    try {
      this.calculateTime();
      this.confirmed = true;
      await this.statementManager.concludeQuiz();
      if (
        this.secondsToSubmission == undefined ||
        this.secondsToSubmission <= 0
      ) {
        await this.$router.push({ name: 'quiz-results' });
      } else {
        this.countDownToResults();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateTime() {
    if (this.statementQuiz) {
      this.statementQuiz.answers[this.questionOrder].timeTaken +=
        new Date().getTime() - this.startTime.getTime();
      this.startTime = new Date();
    }
  }

  async countDownToResults() {
    if (this.secondsToSubmission && this.secondsToSubmission > 0) {
      this.secondsToSubmission! -= 1;
      setTimeout(() => {
        this.countDownToResults();
      }, 1000);
    } else {
      await this.endQuiz();
    }
  }

  get HHMMSS() {
    let hours = Math.floor(this.secondsToSubmission / 3600);
    console.log(hours);
    let minutes = Math.floor((this.secondsToSubmission - hours * 3600) / 60);
    let seconds = this.secondsToSubmission - hours * 3600 - minutes * 60;

    let hoursString = hours < 10 ? '0' + hours : hours;
    let minutesString = minutes < 10 ? '0' + minutes : minutes;
    let secondsString = seconds < 10 ? '0' + seconds : seconds;

    return `${hoursString}:${minutesString}:${secondsString}`;
  }
}
</script>
