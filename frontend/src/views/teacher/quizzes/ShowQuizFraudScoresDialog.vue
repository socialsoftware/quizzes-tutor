<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card v-if="quizFraudScores">
      <v-card-title> Fraud Scores </v-card-title>
      <v-card-text>
        {{ quizFraudScores }}
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import QuizFraudScores from '@/models/management/QuizFraudScores';

@Component({})
export default class ShowQuizFraudScoresDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizFraudScores!: QuizFraudScores | null;
}
</script>
