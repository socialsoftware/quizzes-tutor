<template>
  <v-dialog
    :model-value="dialog"
    max-width="70%"
    @update:model-value="$emit('update:dialog', $event)"
    @keydown.esc="$emit('update:dialog', false)"
  >
    <v-card>
      <v-card-title>
        {{ quizAnswer.name }}
      </v-card-title>
      <v-card-subtitle class="quiz-details-navigation">
        <v-icon
          @click="previousQuestion()"
          v-bind:class="[
            'navigation-button',
            currentQuestion === 0 ? 'disabled' : '',
          ]"
          >fas x-small fa-chevron-left</v-icon
        >
        <span>{{
          quizAnswer.questionAnswers[currentQuestion].question.title
        }}</span>
        <v-icon
          @click="nextQuestion()"
          v-bind:class="[
            'navigation-button',
            currentQuestion < quizAnswer.questionAnswers.length - 1
              ? ''
              : 'disabled',
          ]"
          >fas x-small fa-chevron-right</v-icon
        >
      </v-card-subtitle>
      <v-card-text class="quiz-details-question">
        <show-question
          :question="quizAnswer.questionAnswers[currentQuestion].question"
          :answer="quizAnswer.questionAnswers[currentQuestion].answerDetails"
        />
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" text @click="$emit('update:dialog', false)"> Close </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

const props = defineProps<{
  dialog: boolean;
  quizAnswer: QuizAnswer;
  questionNumber?: number;
}>();

defineEmits(['update:dialog']);

const currentQuestion = ref(0);

onMounted(() => {
  currentQuestion.value = props.questionNumber || 0;
});

const previousQuestion = () => {
  if (currentQuestion.value > 0) {
    currentQuestion.value -= 1;
  }
};

const nextQuestion = () => {
  if (currentQuestion.value < props.quizAnswer.questionAnswers.length - 1) {
    currentQuestion.value += 1;
  }
};
</script>

<style lang="scss" scoped>
.navigation-button.disabled {
  cursor: not-allowed !important;
  filter: opacity(0.5);
}
.quiz-details-navigation {
  margin-top: 0 !important;
  display: flex;
  justify-content: space-around;

  & span {
    flex-grow: 2;
  }
}

.quiz-details-question {
  text-align: left;
}
</style>
