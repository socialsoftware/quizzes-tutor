<template>
  <v-container v-if="weeklyScores != null" fluid>
    <h3>Weekly Scores</h3>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="weeklyScores"
        :sort-by="['week']"
        :sort-desc="[true]"
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

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import WeeklyScore from '@/models/dashboard/WeeklyScore';

@Component
export default class WeeklyScoresView extends Vue {
  @Prop() readonly dashboardId!: number;

  weeklyScores: WeeklyScore[] = [];

  headers: object = [
    {
      text: 'Week',
      value: 'week',
      align: 'start',
      width: '5px',
    },
    {
      text: 'Quizzes Answered',
      value: 'quizzesAnswered',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Questions Answered',
      value: 'questionsAnswered',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Questions Uniquely Answered',
      value: 'questionsUniquelyAnswered',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Percentage Correct',
      value: 'percentageCorrect',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Improved Correct Questions',
      value: 'improvedCorrectAnswers',
      align: 'center',
      width: '5px',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.weeklyScores = await RemoteServices.updateWeeklyScores(
        this.dashboardId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
