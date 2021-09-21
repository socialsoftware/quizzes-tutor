<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    styles="overflow:hidden"
  >
    <v-card v-if="quizTimeFraudScores">
      <v-card-title> Fraud Scores </v-card-title>
      <v-alert dense type="warning" elevation="2">
        THIS IS A WIP FEATURE, DO NOT CONSIDER THE VALUES BELOW
      </v-alert>

      <div style="margin: 20px">
        <div id="fraud-communications-container">
          <fraud-violin
            graphId="1"
            :quizFraudScores="quizTimeFraudScores"
          ></fraud-violin>
        </div>
        <fraud-violin
          graphId="2"
          :quizFraudScores="quizCommunicationFraudScoresIn"
        ></fraud-violin>

        <fraud-violin
          graphId="3"
          :quizFraudScores="quizCommunicationFraudScoresOut"
        ></fraud-violin>
      </div>
      <v-data-table
        :headers="headers"
        :items="quizTimeFraudScores.fraudScores"
        :sort-by="['score']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      />
      <v-data-table
        :headers="headers"
        :items="quizCommunicationFraudScoresIn.fraudScores"
        :sort-by="['score']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      />
      <v-data-table
        :headers="headers"
        :items="quizCommunicationFraudScoresOut.fraudScores"
        :sort-by="['score']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      />
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import QuizFraudScores from '@/models/management/fraud/QuizFraudScores';
import FraudViolin from '@/views/teacher/fraud/FraudViolin.vue';
@Component({ components: { 'fraud-violin': FraudViolin } })
export default class ShowQuizFraudScoresDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizTimeFraudScores!: QuizFraudScores | null;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizCommunicationFraudScoresIn!: QuizFraudScores | null;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizCommunicationFraudScoresOut!: QuizFraudScores | null;
  headers: any[] = [
    { text: 'Username', value: 'username' },
    { text: 'Score', value: 'score' },
  ];
}
</script>
