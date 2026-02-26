<template>
  <div class="question-container" v-if="question && question.questionDetails">
    <div class="question">
      <span
        v-if="backsies"
        class="square"
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDownText(question.content || '', question.image)"
      ></div>
      <div class="square" @click="increaseOrder" data-cy="nextQuestionButton">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <component
      :is="question.questionDetails.type === 'multiple_choice' ? MultipleChoiceAnswer : (question.questionDetails.type === 'code_fill_in' ? CodeFillInAnswer : CodeOrderAnswer)"
      :questionDetails="question.questionDetails as any"
      :answerDetails="answer?.answerDetails as any"
      @question-answer-update="$emit('question-answer-update', $event)"
    >
    </component>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import StatementQuestion from '@/models/statement/StatementQuestion';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import MultipleChoiceAnswer from '@/components/multiple-choice/MultipleChoiceAnswer.vue';
import StatementAnswer from '@/models/statement/StatementAnswer';
import CodeFillInAnswer from '@/components/code-fill-in/CodeFillInAnswer.vue';
import CodeOrderAnswer from '@/components/code-order/CodeOrderAnswer.vue';

const props = defineProps<{
  questionOrder: number;
  question?: StatementQuestion;
  answer?: StatementAnswer;
  questionNumber: number;
  backsies: boolean;
}>();

const emit = defineEmits([
  'update:questionOrder',
  'increase-order',
  'decrease-order',
  'question-answer-update',
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

<style lang="scss" scoped />
