<template>
  <ol>
    <li
      v-for="(question, index) in questions"
      :key="question.sequence || index"
      class="text-left"
    >
      <show-question :question="question" />
    </li>
  </ol>
</template>

<script setup lang="ts">
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

defineProps<{
  questions: Question[];
}>();
</script>
