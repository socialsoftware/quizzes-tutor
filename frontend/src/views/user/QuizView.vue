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
            'management-button',
            index === order + 1 ? 'current-management-button' : ''
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
      :optionId="this.quiz.answers.get(this.order).optionId"
      :question="quiz.questions.get(order)"
      :key="order + optionKey"
      @increase-order="increaseOrder"
      @select-option="changeAnswer"
      @decrease-order="decreaseOrder"
    ></question-component>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

Component.registerHooks([
  "beforeRouteEnter",
  "beforeRouteUpdate",
  "beforeRouteLeave"
]);

import Quiz from "../../models/Quiz";
import Answer from "@/models/Answer";
import QuestionComponent from "@/components/QuestionComponent.vue";

@Component({
  components: {
    "question-component": QuestionComponent
  }
})
export default class QuizView extends Vue {
  quiz: Quiz = Quiz.getInstance;
  order: number = 0;
  optionKey: number = 0;

  constructor() {
    super();
  }

  async beforeMount() {
    if (this.quiz.isEmpty()) {
      this.$router.push("/setup");
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
    if (this.quiz.answers.get(this.order) != undefined) {
      if (this.quiz.answers.get(this.order).optionId === optionId) {
        this.quiz.answers.get(this.order).optionId = null;
      } else if (this.quiz.answers.get(this.order)) {
        this.quiz.answers.get(this.order).optionId = optionId;
      }
      this.optionKey += 1;
    }
  }

  /*beforeRouteLeave(to: any, from: any, next: any) {
    this.$dialog
      .confirm("Do you want to proceed?")
      .then(function() {
        next();
      })
      .catch(function() {
        next(false);
      });
  }*/

  async endQuiz() {
    await this.quiz.getCorrectAnswers();
    this.$router.push("/results");
  }
}
</script>
