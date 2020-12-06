<template>
  <v-dialog
        v-model="detailDialog"
        max-width="500px"
        @input="$emit('dialog', false)"
        @keydown.esc="$emit('dialog', false)"
      >
        <v-card>
          <v-card-title>
            {{ quizAnswer.name }}
          </v-card-title>
          <v-card-subtitle>
            {{ quizAnswer.questionAnswers[currentQuestion].question.title }}
          </v-card-subtitle>
          <v-card-actions>
            <v-btn
              color="primary"
              text
              @click="dialog2 = false"
            >
              Close
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';

@Component
export default class ShowQuizAnswersDetailsDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswer!: QuizAnswer;
  @Prop({ required: false }) readonly questionNumber?: number;

  currentQuestion?: number;
  detailDialog?: boolean;

  created() {
    this.currentQuestion = this.questionNumber || 0;
    this.detailDialog = this.dialog;
  }
  
}
</script>


<style lang="scss">
</style>