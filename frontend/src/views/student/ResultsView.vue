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
      v-if="quiz.correctAnswers[order]"
      v-model="order"
      :answer="quiz.answers[order]"
      :correctAnswer="quiz.correctAnswers[order]"
      :question="quiz.questions[order]"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    ></result-component>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import StatementQuiz from "@/models/statement/StatementQuiz";
import ResultComponent from "@/views/components/ResultComponent.vue";

@Component({
  components: {
    "result-component": ResultComponent
  }
})
export default class ResultsView extends Vue {
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
}
</script>
