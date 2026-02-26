<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('close')"
    @keydown.esc="$emit('close')"
    max-width="75%"
  >
    <v-card>
      <v-card-text>
        <ol>
          <li
            v-for="(question, index) in questions"
            :key="question.id || index"
            class="text-left"
          >
            {{ question.status }}
            <v-card-text
              class="text-left"
              :class="{ red: question.status !== 'AVAILABLE' }"
            >
              <show-question :question="question" />
            </v-card-text>
          </li>
        </ol>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn class="text-white" color="blue darken-1" @click="$emit('close')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

defineProps<{
  dialog: boolean;
  questions: Question[];
}>();

defineEmits(['close']);
</script>
