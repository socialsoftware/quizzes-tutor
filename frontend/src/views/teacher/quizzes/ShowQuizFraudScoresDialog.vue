<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card v-if="quizFraudScores">
      <v-card-title> Fraud Scores </v-card-title>
      <div width="75%">
      <div id="violinScores">
      </div>
        <v-data-table
          :headers="headers"
          :items="quizFraudScores.fraudScores"
          :sort-by="['score']"
          sort-desc
          :mobile-breakpoint="0"
          :items-per-page="15"
          :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        />
      </div>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import QuizFraudScores from '@/models/management/QuizFraudScores';
const Plotly = require('plotly.js-cartesian-dist');
@Component({})
export default class ShowQuizFraudScoresDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizFraudScores!: QuizFraudScores | null;
  headers: any[] = [
    { text: 'Username', value: 'username' },
    { text: 'Score', value: 'score' },
  ];
  mounted() {
    var data = [
      {
        type: 'violin',
        y: this.quizFraudScores?.fraudScores.map((qfs) => qfs.score),
        points: 'all',
        box: {
          visible: true,
        },
        boxpoints: true,
        line: {
          color: 'black',
        },
        fillcolor: '#1876d1',
        opacity: 0.6,
        meanline: {
          visible: true,
        },
        x0: 'Scores',
      },
    ];

    var layout = {
      title: '',
      yaxis: {
        zeroline: false,
      },
    };

    Plotly.newPlot('violinScores', data, layout);
  }
}
</script>
