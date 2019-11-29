<template>
  <div
    tabindex="0"
    class="master-container"
    @keydown.left="decreaseOrder"
    @keydown.right="increaseOrder"
  >
    <header>
      <!--span class="timer"><i class="fas fa-clock"></i> 00:00</span-->
      <span class="end-quiz" @click="endQuiz"
        ><i class="fas fa-times" />End Quiz</span
      >
    </header>

    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
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
        v-if="order !== statementManager.statementQuiz.questions.length - 1"
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <question-component
      v-model="order"
      :optionId="statementManager.answers[order].optionId"
      :question="statementManager.statementQuiz.questions[order]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @select-option="changeAnswer"
      @decrease-order="decreaseOrder"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import QuestionComponent from "@/views/components/QuestionComponent.vue";
import StatementManager from "@/models/statement/StatementManager";

Component.registerHooks([
  "beforeRouteEnter",
  "beforeRouteUpdate",
  "beforeRouteLeave"
]);

@Component({
  components: {
    "question-component": QuestionComponent
  }
})
export default class QuizView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  startTime: Date = new Date();
  order: number = 0;

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: "create-quiz" });
    }
  }

  increaseOrder(): void {
    if (
      this.order + 1 <
      +this.statementManager.statementQuiz!.questions.length
    ) {
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
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.calculateTime();
      this.order = n;
    }
  }

  changeAnswer(optionId: number) {
    if (this.statementManager.answers[this.order]) {
      if (this.statementManager.answers[this.order].optionId === optionId) {
        this.statementManager.answers[this.order].optionId = null;
      } else {
        this.statementManager.answers[this.order].optionId = optionId;
      }
    }
  }

  async endQuiz() {
    try {
      this.calculateTime();
      await this.statementManager.getCorrectAnswers();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$router.push({ name: "quiz-results" });
  }

  calculateTime() {
    this.statementManager.answers[this.order].timeTaken +=
      new Date().getTime() - this.startTime.getTime();
    this.startTime = new Date();
  }
}
</script>
