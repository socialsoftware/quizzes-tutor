<template>
  <div class="master-container">
    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === order + 1 ? 'current-question-button' : '',
            index === order + 1 &&
            statementManager.correctAnswers[index - 1].correctOptionId !==
              statementManager.answers[index - 1].optionId
              ? 'incorrect-current'
              : '',
            statementManager.correctAnswers[index - 1].correctOptionId !==
            statementManager.answers[index - 1].optionId
              ? 'incorrect'
              : ''
          ]"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          {{ index }}
        </span>
      </div>
      <span class="left-button" @click="decreaseOrder" v-if="order !== 0"
        ><i class="fas fa-chevron-left"></i
      ></span>
      <span
        class="right-button"
        @click="increaseOrder"
        v-if="order !== statementManager.statementQuiz.questions.length - 1"
        ><i class="fas fa-chevron-right"></i
      ></span>
    </div>
    <result-component
      v-model="order"
      :answer="statementManager.answers[order]"
      :correctAnswer="statementManager.correctAnswers[order]"
      :question="statementManager.statementQuiz.questions[order]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    ></result-component>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import StatementManager from "@/models/statement/StatementManager";
import ResultComponent from "@/views/components/ResultComponent.vue";

@Component({
  components: {
    "result-component": ResultComponent
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
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
      this.order += 1;
    }
  }

  decreaseOrder(): void {
    if (this.order > 0) {
      this.order -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.order = n;
    }
  }
}
</script>

<style>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>
