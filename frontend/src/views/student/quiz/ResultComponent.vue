<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      !answer.isQuestionAnswered() ? 'unanswered' : '',
      answer.isQuestionAnswered() && answer.isAnswerCorrect(correctAnswer)
        ? 'correct-question'
        : 'incorrect-question',
    ]"
  >
    <div class="question">
      <span
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
        class="square"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDownText(question.content || '', question.image)"
      ></div>
      <div @click="increaseOrder" class="square" data-cy="nextQuestionButton">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <component
      :is="question.questionDetails.type === 'multiple_choice' ? MultipleChoiceAnswer : (question.questionDetails.type === 'code_fill_in' ? CodeFillInAnswerResult : CodeOrderAnswerResult)"
      :questionDetails="question.questionDetails as any"
      :answerDetails="answer.answerDetails as any"
      :correctAnswerDetails="correctAnswer.correctAnswerDetails as any"
    >
    </component>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import MultipleChoiceAnswer from '@/components/multiple-choice/MultipleChoiceAnswer.vue';
import CodeFillInAnswerResult from '@/components/code-fill-in/CodeFillInAnswerResult.vue';
import CodeOrderAnswerResult from '@/components/code-order/CodeOrderAnswerResult.vue';

const props = defineProps<{
  questionOrder: number;
  question: StatementQuestion;
  correctAnswer: StatementCorrectAnswer;
  answer: StatementAnswer;
  questionNumber: number;
}>();

const emit = defineEmits([
  'update:questionOrder',
  'increase-order',
  'decrease-order',
]);

const hover = ref(false);

const increaseOrder = () => {
  emit('increase-order');
};

const decreaseOrder = () => {
  emit('decrease-order');
};

const convertMarkDownText = (text: string, image: Image | null = null): string => {
  return convertMarkDown(text, image);
};
</script>

<style lang="scss" scoped>
.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
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
}
</style>
