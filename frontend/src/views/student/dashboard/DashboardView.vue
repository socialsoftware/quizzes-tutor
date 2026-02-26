<template>
  <div class="container">
    <h2>Dashboard</h2>

    <v-card class="table" outlined color="transparent">
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

    <div v-if="dashboardId && show === 'Global'" class="stats-container">
      <global-stats-view :dashboardId="dashboardId"></global-stats-view>
    </div>

    <div v-if="dashboardId && show === 'Weekly'">
      <weekly-scores-view :dashboardId="dashboardId"></weekly-scores-view>
    </div>

    <div v-if="dashboardId && show === 'Failed'">
      <failed-answers-view :dashboardId="dashboardId"></failed-answers-view>
    </div>

    <div v-if="dashboardId && show === 'Difficult'">
      <difficult-questions-view
        :dashboardId="dashboardId"
      ></difficult-questions-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import GlobalStatsView from '@/views/student/dashboard/GlobalStatsView.vue';
import DifficultQuestionsView from '@/views/student/dashboard/DifficultQuestionsView.vue';
import WeeklyScoresView from '@/views/student/dashboard/WeeklyScoresView.vue';
import FailedAnswersView from '@/views/student/dashboard/FailedAnswersView.vue';

const store = useStore();

const dashboardId = ref<number | null>(null);
const show = ref<string | null>(null);

onMounted(async () => {
  store.setLoading();
  try {
    let dashboard = await RemoteServices.getUserDashboard();

    dashboardId.value = dashboard.id;
    show.value = 'Global';
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});
</script>
