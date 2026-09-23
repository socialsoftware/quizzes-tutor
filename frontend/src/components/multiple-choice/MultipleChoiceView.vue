<template>
  <ul>
    <li v-for="option in questionDetails.options" :key="option.id!">
      <span
        v-if="option.correct"
        v-html="
          convertMarkDown(
            studentAnswered(option.id!) + '**[★]** ' + option.content
          )
        "
        v-bind:class="[option.correct ? 'font-weight-bold' : '']"
      />
      <span
        v-else
        v-html="convertMarkDown(studentAnswered(option.id!) + option.content)"
      />
    </li>
  </ul>
</template>

<script setup lang="ts">
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';
import MultipleChoiceAnswerDetails from '@/models/management/questions/MultipleChoiceAnswerDetails';

const props = defineProps<{
  questionDetails: MultipleChoiceQuestionDetails;
  answerDetails?: MultipleChoiceAnswerDetails;
}>();

const studentAnswered = (option: number) => {
  return props.answerDetails && props.answerDetails?.option.id === option
    ? '**[S]** '
    : '';
};
</script>
