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
            {{ dashboard != null ? dashboard.lastCheckWeeklyScores : '-' }}
          </v-btn>
        </v-col>
        <v-col>
          <v-btn
            color="primary"
            dark
            data-cy="failedAnswersMenuButton"
            v-on:click="show = 'Failed'"
            >Failed Answers <br />
            {{ dashboard != null ? dashboard.lastCheckFailedAnswers : '-' }}
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
              dashboard != null ? dashboard.lastCheckDifficultQuestions : '-'
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
        :dashboard="dashboard"
        v-on:refresh="onWeeklyScoresRefresh"
      ></weekly-scores-view>
    </div>

    <div v-if="show === 'Failed'">
      <failed-answers-view
        :dashboard="dashboard"
        v-on:refresh="onFailedAnswersRefresh"
      ></failed-answers-view>
    </div>

    <div v-if="show === 'Difficult'">
      <difficult-questions-view
        :dashboard="dashboard"
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
import Dashboard from '@/models/dashboard/Dashboard';
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
  dashboard!: Dashboard;
  show: string = 'Global';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.dashboard = await RemoteServices.getUserDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onWeeklyScoresRefresh(date: string) {
    this.dashboard.lastCheckWeeklyScores = date;
  }

  onFailedAnswersRefresh(date: string) {
    this.dashboard.lastCheckFailedAnswers = date;
  }

  onDifficultQuestionsRefresh(date: string) {
    this.dashboard.lastCheckDifficultQuestions = date;
  }
}
</script>
