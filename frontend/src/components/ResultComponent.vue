<template>
  <div class="question-container" v-if="question">
    <div class="question">
      <span class="square" @click="decreaseOrder">
        <span>{{ order + 1 }}</span></span
      >
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div class="square" @click="increaseOrder">
        <i class="fas fa-chevron-right"></i>
      </div>
    </div>
    <ul class="option-list">
      <li
        v-bind:class="[
          correctAnswer.correctOption === 0 ? 'correct' : '',
          answer.option === 0 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">A</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[0].content)"
        ></span>
      </li>
      <li
        v-bind:class="[
          correctAnswer.correctOption === 1 ? 'correct' : '',
          answer.option === 1 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">B</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[1].content)"
        ></span>
      </li>
      <li
        v-bind:class="[
          correctAnswer.correctOption === 2 ? 'correct' : '',
          answer.option === 2 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">C</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[2].content)"
        ></span>
      </li>
      <li
        v-bind:class="[
          correctAnswer.correctOption === 3 ? 'correct' : '',
          answer.option === 3 ? 'wrong' : '',
          'option'
        ]"
      >
        <span class="option-letter">D</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[3].content)"
        ></span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import Question from "@/models/Question";
import Answer from "@/models/Answer";
import CorrectAnswer from "@/models/CorrectAnswer";
import { convertMarkDown } from "@/scripts/script"


@Component
export default class ResultComponent extends Vue {
  @Model("order", Number) order: number | undefined;
  @Prop(Question) readonly question: Question | undefined;
  @Prop(CorrectAnswer) readonly correctAnswer!: CorrectAnswer;
  @Prop(Answer) readonly answer: Answer | undefined;

  constructor() {
    super();
  }

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }
}
</script>

<style lang="scss">
.wrong {
  .option-content {
    background-color: #cf2323;
    color: rgb(255, 255, 255) !important;
  }

  .option-letter {
    background-color: #cf2323 !important;
    color: rgb(255, 255, 255) !important;
  }
}

.correct {
  .option-content {
    background-color: #299455;
    color: rgb(255, 255, 255) !important;
  }

  .option-letter {
    background-color: #299455 !important;
    color: rgb(255, 255, 255) !important;
  }
}
</style>
