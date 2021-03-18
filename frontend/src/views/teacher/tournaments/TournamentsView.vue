<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      disable-pagination
      :mobile-breakpoint="0"
      multi-sort
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      data-cy="allTournaments"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
        </v-card-title>
      </template>
      <template v-slot:[`item.id`]="{ item }">
        <v-chip color="primary" small @click="openTournamentDashboard(item)">
          <span> {{ item.id }} </span>
        </v-chip>
      </template>
      <template v-slot:[`item.topics`]="{ item }">
        <view-tournament-topics :tournament="item" />
      </template>
      <template v-slot:[`item.times`]="{ item }">
        <v-chip x-small>
          {{ item.startTime }}
        </v-chip>
        <v-chip x-small>
          {{ item.endTime }}
        </v-chip>
      </template>
      <template v-slot:[`item.isCanceled`]="{ item }">
        <v-chip :color="item.getStateColor()">
          {{ item.getStateName() }}
        </v-chip>
      </template>
      <template v-slot:[`item.privateTournament`]="{ item }">
        <v-chip :color="item.getPrivateColor()">
          {{ item.getPrivateName() }}
        </v-chip>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on tournament's number to
      view it.
    </footer>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/user/Tournament';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'view-tournament-topics': ViewTournamentTopics,
  },
})
export default class TournamentsView extends Vue {
  tournaments: Tournament[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Course Acronym',
      value: 'courseAcronym',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Tournament Number',
      value: 'id',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      width: '10%',
    },
    {
      text: 'State',
      value: 'isCanceled',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Privacy',
      value: 'privateTournament',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Start/End Time',
      value: 'times',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getTournamentsForCourseExecution();
      this.tournaments.sort((a, b) => this.sortById(a, b));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  sortById(a: Tournament, b: Tournament) {
    if (a.id && b.id) return a.id > b.id ? 1 : -1;
    else return 0;
  }

  async openTournamentDashboard(tournament: Tournament) {
    this.$emit('close-show-dashboard-dialog', false);
    if (tournament)
      await this.$router.push({
        path: '/teacher/tournament',
        query: { id: tournament.id.toString() },
      });
  }
}
</script>

<style lang="scss" scoped></style>
