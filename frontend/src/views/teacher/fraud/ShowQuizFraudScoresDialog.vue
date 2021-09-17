<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card v-if="quizFraudScores">
      <v-card-title> Fraud Scores </v-card-title>
      <div style="margin: 0px 5px">
        <v-alert dense type="warning" elevation="2">
          THIS IS A WIP FEATURE, DO NOT CONSIDER THE VALUES BELOW
        </v-alert>

        <div width="75%">
          <v-container>
            <v-row>
              <v-col>
                <v-card style="display:flex; justify-content:center"
                  ><fraud-violin
                    :quizFraudScores="quizFraudScores"
                  ></fraud-violin
                ></v-card>
              </v-col>
            </v-row>
          </v-container>

          <v-data-table
            :headers="headers"
            :items="quizFraudScores.fraudScores"
            :sort-by="['score']"
            sort-desc
            :mobile-breakpoint="0"
            :items-per-page="15"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          />
          <v-data-table
            :headers="headers"
            :items="quizGraphFraudScoresIn.fraudScores"
            :sort-by="['score']"
            sort-desc
            :mobile-breakpoint="0"
            :items-per-page="15"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          />
          <v-data-table
            :headers="headers"
            :items="quizGraphFraudScoresOut.fraudScores"
            :sort-by="['score']"
            sort-desc
            :mobile-breakpoint="0"
            :items-per-page="15"
            :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
          />
        </div>
      </div>
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
  readonly quizFraudScores!: QuizFraudScores | null;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizGraphFraudScoresIn!: QuizFraudScores | null;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizGraphFraudScoresOut!: QuizFraudScores | null;
  headers: any[] = [
    { text: 'Username', value: 'username' },
    { text: 'Score', value: 'score' },
  ];
}
</script>
