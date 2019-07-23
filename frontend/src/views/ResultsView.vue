<template>
  <div class="master-container">
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
    <result-component
      v-if="quiz.correctAnswers.get(order)"
      v-model="order"
      :answer="quiz.answers.get(order)"
      :correctAnswer="quiz.correctAnswers.get(order)"
      :question="quiz.questions.get(order)"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    ></result-component>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Quiz from "../models/Quiz";
import ResultComponent from "@/components/ResultComponent.vue";

@Component({
  components: {
    "result-component": ResultComponent
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
}
</script>
