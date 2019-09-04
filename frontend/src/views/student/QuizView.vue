<template>
  <div class="master-container">
    <header>
      <span class="timer"><i class="fas fa-clock"></i> 00:00</span>
      <span class="end-quiz" @click="endQuiz"
        ><i class="fas fa-times"></i>End Quiz</span
      >
    </header>

    <div class="question-navigation">
      <span class="left-button" @click="decreaseOrder"
        ><i class="fas fa-chevron-left"></i
      ></span>
      <div class="navigation-buttons">
        <span
          v-for="index in +quiz.numberOfQuestions"
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
      <span class="right-button" @click="increaseOrder"
        ><i class="fas fa-chevron-right"></i
      ></span>
    </div>
    <question-component
      v-model="order"
      :optionId="quiz.answers[order].optionId"
      :question="quiz.questions[order]"
      @increase-order="increaseOrder"
      @select-option="changeAnswer"
      @decrease-order="decreaseOrder"
    ></question-component>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import QuestionComponent from "@/views/components/QuestionComponent.vue";
import StatementQuiz from "@/models/statement/StatementQuiz";

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
  quiz: StatementQuiz = StatementQuiz.getInstance;
  order: number = 0;

  constructor() {
    super();
  }

  // noinspection JSUnusedGlobalSymbols
  async beforeMount() {
    if (this.quiz.isEmpty()) {
      await this.$router.push("/setup");
    }
  }

  increaseOrder(): void {
    if (this.order + 1 < +this.quiz.numberOfQuestions) {
      this.order += 1;
    }
  }

  decreaseOrder(): void {
    if (this.order > 0) {
      this.order -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.quiz.numberOfQuestions) {
      this.order = n;
    }
  }

  changeAnswer(optionId: number) {
    if (this.quiz.answers[this.order]) {
      if (this.quiz.answers[this.order].optionId === optionId) {
        this.quiz.answers[this.order].optionId = null;
      } else {
        this.quiz.answers[this.order].optionId = optionId;
      }
    }
  }

  async endQuiz() {
    await this.quiz.getCorrectAnswers();
    await this.$router.push("/results");
  }
}
</script>
