<template>
  <div>
    <span v-html="convertMarkDown(question.content, question.image)" />
    <br />
    <component
      :is="componentMap[question.questionDetailsDto.type]"
      :questionDetails="question.questionDetailsDto"
      :answerDetails="answer"
    />
    <br />
  </div>
</template>

<script setup lang="ts">
import { convertMarkDown as convertMarkDownService } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import MultipleChoiceView from '@/components/multiple-choice/MultipleChoiceView.vue';
import CodeFillInView from '@/components/code-fill-in/CodeFillInView.vue';
import CodeOrderView from '@/components/code-order/CodeOrderView.vue';
import AnswerDetails from '@/models/management/questions/AnswerDetails';

const props = defineProps<{
  question: Question;
  answer?: AnswerDetails;
}>();

const componentMap: Record<string, any> = {
  multiple_choice: MultipleChoiceView,
  code_fill_in: CodeFillInView,
  code_order: CodeOrderView,
};

const convertMarkDown = (text: string, image: Image | null = null): string => {
  return convertMarkDownService(text, image);
};
</script>
