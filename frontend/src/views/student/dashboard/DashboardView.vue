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
            >Weekly Scores <br />
            {{ lastCheckWeeklyScores != null ? lastCheckWeeklyScores : '-' }}
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="failedAnswersMenuButton"
            v-on:click="show = 'Failed'"
            >Failed Answers <br />
            {{ lastCheckFailedAnswers != null ? lastCheckFailedAnswers : '-' }}
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="difficultQuestionsMenuButton"
            v-on:click="show = 'Difficult'"
            >Difficult Questions <br />
            {{
              lastCheckDifficultQuestions != null
                ? lastCheckDifficultQuestions
                : '-'
            }}
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <div v-if="show === 'Global'" class="stats-container">
      <global-stats-view></global-stats-view>
    </div>

    <div v-if="show === 'Weekly'">
      <weekly-scores-view
        :dashboardId="dashboardId"
        v-on:refresh="onWeeklyScoresRefresh"
      ></weekly-scores-view>
    </div>

    <div v-if="show === 'Failed'">
      <failed-answers-view
        :dashboardId="dashboardId"
        :lastCheckFailedAnswers="lastCheckFailedAnswers"
        v-on:refresh="onFailedAnswersRefresh"
      ></failed-answers-view>
    </div>

    <div v-if="show === 'Difficult'">
      <difficult-questions-view
        :dashboardId="dashboardId"
        v-on:refresh="onDifficultQuestionsRefresh"
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
  lastCheckWeeklyScores: string | null = null;
  lastCheckFailedAnswers: string | null = null;
  lastCheckDifficultQuestions: string | null = null;
  show: string = 'Global';

  async created() {
    await this.$store.dispatch('loading');
    try {
      let dashboard = await RemoteServices.getUserDashboard();

      this.dashboardId = dashboard.id;
      this.lastCheckWeeklyScores = dashboard.lastCheckWeeklyScores;
      this.lastCheckFailedAnswers = dashboard.lastCheckFailedAnswers;
      this.lastCheckDifficultQuestions = dashboard.lastCheckDifficultQuestions;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onWeeklyScoresRefresh(date: string) {
    this.lastCheckWeeklyScores = date;
  }

  onFailedAnswersRefresh(date: string) {
    this.lastCheckFailedAnswers = date;
  }

  onDifficultQuestionsRefresh(date: string) {
    this.lastCheckDifficultQuestions = date;
  }
}
</script>
