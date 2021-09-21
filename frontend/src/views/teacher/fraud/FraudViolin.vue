<template>
  <div class="graph-container">
    <div :id="graphId" ></div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import QuizFraudScores from '@/models/management/fraud/QuizFraudScores';
const Plotly = require('plotly.js-cartesian-dist');
@Component({})
export default class FraudViolin extends Vue {
  @Prop({ type: QuizFraudScores, required: true })
  readonly quizFraudScores!: QuizFraudScores | null;
  @Prop({required: true })
  readonly graphId!:string;
  
  mounted() {
    let data = [
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
        text: this.quizFraudScores?.fraudScores.map(
          (qfs) => `(Username, ${qfs.username})`
        ),
        fillcolor: '#1876d1',
        opacity: 0.6,
        meanline: {
          visible: true,
        },
        x0: 'Scores',
      },
    ];

    let layout = {
      title: '',
      autosize: false,
      width: 0.3,
      yaxis: {
        zeroline: false,
      },
    };

    Plotly.newPlot(this.graphId, data, layout,{responsive: true});
  }
}
</script>

<style>

</style>
