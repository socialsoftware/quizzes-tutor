<template>
  <v-container v-if="weeklyScores != null" fluid>
    <v-card class="table">
      <v-container>
        <v-row>
          <v-col><h2>Weekly Scores</h2></v-col>
          <v-col class="text-right">
            <v-btn
              color="primary"
              dark
              data-cy="refreshWeeklyScoresMenuButton"
              @click="refresh"
              >Refresh
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
      <v-data-table
        :headers="headers"
        :items="weeklyScores"
        :sort-by="['week']"
        :sort-desc="[true]"
        class="elevation-1"
        data-cy="weeklyScoresTable"
        multi-sort
      >
        <template v-slot:[`item.action`]="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                data-cy="deleteWeeklyScoreButton"
                @click="deleteWeeklyScore(item)"
                v-on="on"
                >delete
              </v-icon>
            </template>
            <span>Delete Weekly Score</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ISOtoString } from '@/services/ConvertDateService';
import WeeklyScore from '@/models/dashboard/WeeklyScore';

@Component
export default class WeeklyScoresView extends Vue {
  @Prop() readonly dashboardId!: number;

  weeklyScores: WeeklyScore[] = [];

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    {
      text: 'Week',
      value: 'week',
      align: 'start',
      width: '5px',
    },
    {
      text: 'Number Answered',
      value: 'numberAnswered',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Uniquely Answered',
      value: 'uniquelyAnswered',
      align: 'center',
      width: '5px',
    },
    {
      text: 'Percentage Answered',
      value: 'percentageCorrect',
      align: 'center',
      width: '5px',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.weeklyScores = await RemoteServices.getWeeklyScores(
        this.dashboardId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async refresh() {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.updateWeeklyScores(this.dashboardId);
      this.weeklyScores = await RemoteServices.getWeeklyScores(
        this.dashboardId
      );
      this.$emit('refresh', ISOtoString(new Date().toString()));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteWeeklyScore(toDeleteWeeklyScore: WeeklyScore) {
    try {
      await RemoteServices.deleteWeeklyScore(toDeleteWeeklyScore.id);
      this.weeklyScores = this.weeklyScores.filter(
        (weeklyScore) => weeklyScore.id != toDeleteWeeklyScore.id
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
