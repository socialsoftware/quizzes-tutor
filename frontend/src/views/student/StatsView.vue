<template>
  <div v-if="stats != null">
    <p>Total quizzes done: {{ stats.totalQuizzes }}</p>
    <p>Total questions done: {{ stats.totalAnswers }}</p>
    <chart :stats="stats"></chart>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import StudentStats from "@/models/statement/StudentStats";
import RemoteServices from "@/services/RemoteServices";
import Chart from "@/views/components/Chart.vue";

@Component({
  components: { Chart }
})
export default class StatsView extends Vue {
  stats: StudentStats | null = null;

  // noinspection JSUnusedGlobalSymbols
  async mounted() {
    try {
      this.stats = await RemoteServices.getUserStats();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }
}
</script>
