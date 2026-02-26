<!--
Used on:
  - QuestionComponent.vue
  - ResultComponent.vue
-->

<template>
  <ul data-cy="optionList" class="option-list">
    <li
      v-for="(n, index) in questionDetails.options.length"
      :key="index"
      v-bind:class="['option', optionClass(index)]"
      @click="
        !isReadonly && selectOption(questionDetails.options[index].optionId)
      "
    >
      <span
        v-if="
          isReadonly &&
          correctAnswerDetails?.correctOptionId ===
            questionDetails.options[index].optionId
        "
        class="fas fa-check option-letter"
      />
      <span
        v-else-if="
          isReadonly &&
          answerDetails.optionId === questionDetails.options[index].optionId
        "
        class="fas fa-times option-letter"
      />
      <span v-else class="option-letter">{{
        String.fromCharCode(65 + index)
      }}</span>
      <span
        class="option-content"
        v-html="convertMarkDown(questionDetails.options[index].content)"
      />
    </li>
  </ul>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import MultipleChoiceStatementAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementAnswerDetails';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';

const props = defineProps<{
  questionDetails: MultipleChoiceStatementQuestionDetails;
  answerDetails: MultipleChoiceStatementAnswerDetails;
  correctAnswerDetails?: MultipleChoiceStatementCorrectAnswerDetails;
}>();

const emit = defineEmits(['question-answer-update']);

const isReadonly = computed(() => {
  return !!props.correctAnswerDetails;
});

const optionClass = (index: number) => {
  if (isReadonly.value) {
    if (
      !!props.correctAnswerDetails &&
      props.correctAnswerDetails.correctOptionId ===
        props.questionDetails.options[index].optionId
    ) {
      return 'correct';
    } else if (
      props.answerDetails.optionId ===
      props.questionDetails.options[index].optionId
    ) {
      return 'wrong';
    } else {
      return '';
    }
  } else {
    return props.answerDetails.optionId ===
      props.questionDetails.options[index].optionId
      ? 'selected'
      : '';
  }
};

const selectOption = (optionId: number) => {
  if (props.answerDetails.optionId === optionId) {
    props.answerDetails.optionId = null;
  } else {
    props.answerDetails.optionId = optionId;
  }
  emit('question-answer-update', optionId);
};
</script>

<style lang="scss" scoped>
.unanswered {
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
