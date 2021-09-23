<template>
  <div :id="elementId"></div>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import Plotly, { Layout, ViolinData } from 'plotly.js-dist-min';
import QuizFraudScore from '@/models/management/fraud/QuizFraudScore';
@Component({})
export default class FraudViolin extends Vue {
  @Prop({ required: true, default: [] })
  readonly quizFraudScores!: QuizFraudScore[];
  @Prop({ required: true })
  readonly graphId!: string;
  @Prop({ required: true })
  readonly title!: string;

  get scores() {
    return this.quizFraudScores.map((qfs) => qfs.score);
  }

  get labels() {
    return this.quizFraudScores.map((qfs) => `(Username, ${qfs.username})`);
  }

  @Watch('quizFraudScores', { deep: true })
  onQuizFraudScoresChange() {
    this.drawPlot();
  }
  get elementId(){
    return "fraud-graph-" + this.graphId;
  }
  mounted() {
    this.drawPlot();

    
  }

  drawPlot() {
    let data: Partial<ViolinData>[] = [
      {
        type: 'violin',
        y: this.scores,
        points: 'all',
        box: {
          visible: true,
        },
        line: {
          color: 'black',
        },
        text: this.labels,
        fillcolor: '#1876d1',
        opacity: 0.6,
        meanline: {
          visible: true,
        },
        x0: 'Scores',
      },
    ];

    const layout: Partial<Layout> = {
      title: this.title,
      autosize: true,
      yaxis: {
        zeroline: false,
      },
      width: 280,
      showlegend: false,
      margin: {
        r: 25,
        l: 25,
      },
    };

    Plotly.newPlot(this.elementId, data, layout, { responsive: true });
    

  }
}
</script>

<style></style>
