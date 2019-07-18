<template>
  <div v-if="stats">
    <p>Total quizzes done: {{ stats.totalQuizzes }}</p>
    <p>Total questions done: {{ stats.totalAnswers }}</p>
    <canvas ref="canvas" />
  </div>
</template>

<script lang="ts">
import Component, { mixins } from "vue-class-component";
import { HorizontalBar } from "vue-chartjs";
import Stats from "@/models/Stats";

@Component
export default class LineChart extends mixins(HorizontalBar) {
  stats: Stats = new Stats();

  async mounted() {
    this.stats = await Stats.getStats();
    console.log(this.stats.uniqueCorrectAnswers);
    this.renderChart(
      {
        labels: ["All topics"],
        datasets: [
          {
            label: "Correct Answers",
            backgroundColor: "#299455",
            data: [this.stats.uniqueCorrectAnswers]
          },
          {
            label: "Incorrect Answers",
            backgroundColor: "#cf2323",
            data: [this.stats.uniqueWrongAnswers]
          },
          {
            label: "Still to answer questions",
            backgroundColor: "#D5D5D5",
            data: [
              this.stats.totalUniqueQuestions -
                this.stats.uniqueWrongAnswers -
                this.stats.uniqueCorrectAnswers
            ]
          }
        ]
      },
      {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          xAxes: [
            {
              stacked: true,
              categoryPercentage: 10,
              barPercentage: 10
            }
          ],
          yAxes: [
            {
              stacked: true
            }
          ]
        }
      }
    );
  }
}
</script>
