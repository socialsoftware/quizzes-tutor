<template>
  <div class="container">
    <h2>Dashboard</h2>

    <v-card class="table">
      <v-row>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="globalStatisticsMenuButton"
            v-on:click="show = 'Global'"
          >
            Global Statistics
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="weeklyScoresMenuButton"
            v-on:click="show = 'Weekly'"
            >Weekly Scores
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="failedAnswersMenuButton"
            v-on:click="show = 'Failed'"
            >Failed Answers
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="difficultQuestionsMenuButton"
            v-on:click="show = 'Difficult'"
            >Difficult Questions
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <div v-if="show === 'Global'" class="stats-container">
      <global-stats-view :dashboardId="dashboardId"></global-stats-view>
    </div>

    <div v-if="show === 'Weekly'">
      <weekly-scores-view :dashboardId="dashboardId"></weekly-scores-view>
    </div>

    <div v-if="show === 'Failed'">
      <failed-answers-view :dashboardId="dashboardId"></failed-answers-view>
    </div>

    <div v-if="show === 'Difficult'">
      <difficult-questions-view
        :dashboardId="dashboardId"
      ></difficult-questions-view>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import GlobalStatsView from '@/views/student/dashboard/GlobalStatsView.vue';
import DifficultQuestionsView from '@/views/student/dashboard/DifficultQuestionsView.vue';
import WeeklyScoresView from '@/views/student/dashboard/WeeklyScoresView.vue';
import FailedAnswersView from '@/views/student/dashboard/FailedAnswersView.vue';

@Component({
  components: {
    GlobalStatsView,
    WeeklyScoresView,
    FailedAnswersView,
    DifficultQuestionsView,
  },
})
export default class DashboardView extends Vue {
  dashboardId: number | null = null;
  lastCheckDifficultQuestions: string | null = null;
  show: string | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      let dashboard = await RemoteServices.getUserDashboard();

      this.dashboardId = dashboard.id;
      this.lastCheckDifficultQuestions = dashboard.lastCheckDifficultQuestions;
      this.show = 'Global';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
