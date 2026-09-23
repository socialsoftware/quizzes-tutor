<template>
  <v-container v-if="weeklyScores != null" fluid>
    <h3>Weekly Scores</h3>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="weeklyScores"
        :sort-by="[{ key: 'week', order: 'desc' }]"
        class="elevation-1"
        data-cy="weeklyScoresTable"
        multi-sort
      >
        <template v-slot:[`item.percentageCorrect`]="{ item }">
          {{ item.percentageCorrect + '%' }}
        </template>
        <template v-slot:[`item.improvedCorrectAnswers`]="{ item }">
          {{ item.improvedCorrectAnswers + '%' }}
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import WeeklyScore from '@/models/dashboard/WeeklyScore';

const props = defineProps<{
  dashboardId: number;
}>();

const store = useStore();

const weeklyScores = ref<WeeklyScore[]>([]);

const headers: any = [
  {
    title: 'Week',
    key: 'week',
    align: 'start',
    width: '5px',
  },
  {
    title: 'Quizzes Answered',
    key: 'quizzesAnswered',
    align: 'center',
    width: '5px',
  },
  {
    title: 'Questions Answered',
    key: 'questionsAnswered',
    align: 'center',
    width: '5px',
  },
  {
    title: 'Questions Uniquely Answered',
    key: 'questionsUniquelyAnswered',
    align: 'center',
    width: '5px',
  },
  {
    title: 'Percentage Correct',
    key: 'percentageCorrect',
    align: 'center',
    width: '5px',
  },
  {
    title: 'Improved Correct Questions',
    key: 'improvedCorrectAnswers',
    align: 'center',
    width: '5px',
  },
];

onMounted(async () => {
  store.setLoading();
  try {
    weeklyScores.value = await RemoteServices.updateWeeklyScores(
      props.dashboardId
    );
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});
</script>
