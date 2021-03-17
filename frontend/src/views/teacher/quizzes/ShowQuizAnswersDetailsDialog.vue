<template>
  <v-dialog
    v-model="detailDialog"
    max-width="70%"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
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
        <v-btn color="primary" text @click="dialog2 = false"> Close </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

@Component({
  components: {
    ShowQuestion,
  },
})
export default class ShowQuizAnswersDetailsDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswer!: QuizAnswer;
  @Prop({ required: false }) readonly questionNumber?: number;

  currentQuestion: number = 0;
  detailDialog?: boolean;

  created() {
    this.currentQuestion = this.questionNumber || 0;
    this.detailDialog = this.dialog;
  }

  previousQuestion() {
    if (this.currentQuestion > 0) {
      this.currentQuestion = this.currentQuestion - 1;
    }
  }

  nextQuestion() {
    if (this.currentQuestion < this.quizAnswer.questionAnswers.length - 1) {
      this.currentQuestion = this.currentQuestion + 1;
    }
  }
}
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
