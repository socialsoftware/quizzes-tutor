<template>
  <div class="question-container" v-if="question">
    <div class="question">
      <span class="square" @click="decreaseOrder">
        <span>{{ order + 1 }}</span>
        {{ question.quizQuestionId }}
      </span>
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
        v-for="(n, index) in question.options.length"
        :key="index"
        v-bind:class="[
          answer.optionId === question.options[index].optionId ? 'wrong' : '',
          correctAnswer.correctOptionId === question.options[index].optionId
            ? 'correct'
            : '',
          'option'
        ]"
      >
        <span class="option-letter">{{ optionLetters[index] }}</span
        >{{
          correctAnswer.correctOptionId +
            " : " +
            question.options[index].optionId
        }}
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[index].content)"
        ></span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import { convertMarkDown } from "@/scripts/script";
import Question from "@/models/student/Question";
import Answer from "@/models/student/Answer";
import CorrectAnswer from "@/models/student/CorrectAnswer";
import Image from "@/models/student/Image";

@Component
export default class ResultComponent extends Vue {
  @Model("order", Number) order: number | undefined;
  @Prop(Question) readonly question: Question | undefined;
  @Prop(CorrectAnswer) readonly correctAnswer!: CorrectAnswer;
  @Prop(Answer) readonly answer: Answer | undefined;

  optionLetters: string[] = ["A", "B", "C", "D"];

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

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
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
