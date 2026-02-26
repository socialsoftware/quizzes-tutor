<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('update:dialog', $event)"
    @keydown.esc="$emit('update:dialog', false)"
    max-width="75%"
  >
    <v-card v-if="quiz">
      <v-card-title>{{ quiz.title }}</v-card-title>

      <v-card-text>
        <show-question-list :questions="quiz.questions" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('update:dialog', false)">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { Quiz } from '@/models/management/Quiz';
import ShowQuestionList from '@/views/teacher/questions/ShowQuestionList.vue';

defineProps<{
  dialog: boolean;
  quiz: Quiz | null;
}>();

defineEmits(['update:dialog']);
</script>
