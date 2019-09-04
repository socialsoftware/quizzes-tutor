<script lang="ts">
import Component, { mixins } from "vue-class-component";
import { HorizontalBar } from "vue-chartjs";
import { Prop } from "vue-property-decorator";
import StudentStats from "@/models/statement/StudentStats";

@Component
export default class Chart extends mixins(HorizontalBar) {
  @Prop({ required: true }) readonly stats!: StudentStats;

  async mounted() {
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
