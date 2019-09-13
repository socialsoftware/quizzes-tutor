<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      answer.optionId === null ? 'unanswered' : '',
      answer.optionId !== null &&
      correctAnswer.correctOptionId === answer.optionId
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <div class="question">
      <span
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
        class="square"
      >
        <i v-if="hover && order !== 0" class="fas fa-chevron-left"></i>
        <span v-else>{{ order + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div @click="increaseOrder" class="square">
        <i v-if="order !== questionNumber - 1" class="fas fa-chevron-right"></i>
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
        <i
          v-if="
            correctAnswer.correctOptionId === question.options[index].optionId
          "
          class="fas fa-check option-letter"
        ></i>
        <i
          v-else-if="answer.optionId === question.options[index].optionId"
          class="fas fa-times option-letter"
        ></i>
        <span v-else class="option-letter">{{ optionLetters[index] }}</span>
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
import { convertMarkDown } from "@/services/ConvertMarkdownService";
import StatementQuestion from "@/models/statement/StatementQuestion";
import StatementAnswer from "@/models/statement/StatementAnswer";
import StatementCorrectAnswer from "@/models/statement/StatementCorrectAnswer";
import Image from "@/models/management/Image";

@Component
export default class ResultComponent extends Vue {
  @Model("order", Number) order: number | undefined;
  @Prop(StatementQuestion) readonly question!: StatementQuestion;
  @Prop(StatementCorrectAnswer) readonly correctAnswer!: StatementCorrectAnswer;
  @Prop(StatementAnswer) readonly answer!: StatementAnswer;
  @Prop() readonly questionNumber!: number;
  hover: boolean = false;
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
.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .question .question-content {
    background-color: #285f23 !important;
    color: white !important;
  }
  .question .square {
    background-color: #285f23 !important;
    color: white !important;
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
}

.incorrect-question {
  .question .question-content {
    background-color: #761515 !important;
    color: white !important;
  }
  .question .square {
    background-color: #761515 !important;
    color: white !important;
  }
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
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>
