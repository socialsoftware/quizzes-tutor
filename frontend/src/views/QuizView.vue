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
      :answer="quiz.answers.get(order)"
      :question="quiz.questions.get(order)"
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

import Quiz from "../models/Quiz";
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

  changeAnswer(answer: Answer) {
    this.quiz.answers.set(this.order, answer);
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
