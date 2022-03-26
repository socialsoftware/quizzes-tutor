<template>
  <div class="container">
    <h2>Dashboard</h2>

    <v-card class="table">
      <v-row>
        <v-col>
          <v-btn color="primary" dark v-on:click="show = 'Global'">
            Global Statistics
          </v-btn>
        </v-col>
        <v-col>
          <v-btn color="primary" dark v-on:click="show = 'Weekly'"
            >Weekly Scores <br />
            {{ dashboard != null ? dashboard.lastCheckWeeklyScores : '-' }}
          </v-btn>
        </v-col>
        <v-col>
          <v-btn color="primary" dark v-on:click="show = 'Failed'"
            >Failed Answers <br />
            {{ dashboard != null ? dashboard.lastCheckFailedAnswers : '-' }}
          </v-btn>
        </v-col>
        <v-col>
          <v-btn color="primary" dark v-on:click="show = 'Difficult'"
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

@Component({
  components: { GlobalStatsView, DifficultQuestionsView },
})
export default class DashboardView extends Vue {
  dashboard: Dashboard | null = null;
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

  onDifficultQuestionsRefresh(date: string) {
    if (this.dashboard != null)
      this.dashboard.lastCheckDifficultQuestions = date;
  }
}
</script>
