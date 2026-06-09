<template>
  <v-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    @keydown.esc="$emit('update:modelValue', false)"
    max-width="75%"
  >
    <v-card data-cy="showQuestionDialog">
      <v-card-title>
        <span class="headline">{{ question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :question="question" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn class="text-white" color="blue darken-1" @click="$emit('update:modelValue', false)">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

defineProps<{
  modelValue: boolean;
  question: Question;
}>();

defineEmits(['update:modelValue']);
</script>
