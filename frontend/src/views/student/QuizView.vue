<template>
  <div
    tabindex="0"
    class="quiz-container"
    @keydown.left="decreaseOrder"
    @keydown.right="increaseOrder"
  >
    <header>
      <!--span class="timer"><i class="fas fa-clock"></i> 00:00</span-->
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
            index === order + 1 ? 'current-question-button' : ''
          ]"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          {{ index }}
        </span>
      </div>
      <span class="left-button" @click="decreaseOrder" v-if="order !== 0"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button"
        @click="increaseOrder"
        v-if="order !== statementQuiz.questions.length - 1"
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <question-component
      v-model="order"
      v-if="statementQuiz.answers[order]"
      :optionId="statementQuiz.answers[order].optionId"
      :question="statementQuiz.questions[order]"
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
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import QuestionComponent from '@/components/QuestionComponent.vue';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';

Component.registerHooks([
  'beforeRouteEnter',
  'beforeRouteUpdate',
  'beforeRouteLeave'
]);

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
  order: number = 0;

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    }

    if (this.statementQuiz) {
      console.log(this.statementQuiz.questions[this.order]);
    }
  }

  increaseOrder(): void {
    if (this.order + 1 < +this.statementQuiz!.questions.length) {
      this.calculateTime();
      this.order += 1;
    }
  }

  decreaseOrder(): void {
    if (this.order > 0) {
      this.calculateTime();
      this.order -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementQuiz!.questions.length) {
      this.calculateTime();
      this.order = n;
    }
  }

  async changeAnswer(optionId: number) {
    if (this.statementQuiz && this.statementQuiz.answers[this.order]) {
      if (this.statementQuiz.answers[this.order].optionId === optionId) {
        this.statementQuiz.answers[this.order].optionId = null;
      } else {
        this.statementQuiz.answers[this.order].optionId = optionId;
      }

      this.calculateTime();

      try {
        await RemoteServices.submitAnswer(
          this.statementQuiz.id,
          this.statementQuiz.answers[this.order]
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async endQuiz() {
    await this.$store.dispatch('loading');
    try {
      this.calculateTime();
      await this.statementManager.getCorrectAnswers();
      this.confirmed = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$router.push({ name: 'quiz-results' });
    await this.$store.dispatch('clearLoading');
  }

  calculateTime() {
    if (this.statementQuiz) {
      this.statementQuiz.answers[this.order].timeTaken +=
        new Date().getTime() - this.startTime.getTime();
      this.startTime = new Date();
    }
  }
}
</script>
