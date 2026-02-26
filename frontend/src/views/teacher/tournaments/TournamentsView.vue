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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import Tournament from '@/models/user/Tournament';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';
import RemoteServices from '@/services/RemoteServices';

const store = useStore();
const router = useRouter();

const emit = defineEmits(['close-show-dashboard-dialog']);

const tournaments = ref<Tournament[]>([]);
const search = ref('');

const headers: any[] = [
  { title: 'Course Acronym', value: 'courseAcronym', align: 'center', width: '10%' },
  { title: 'Tournament Number', value: 'id', align: 'center', width: '10%' },
  { title: 'Topics', value: 'topics', align: 'center', width: '10%' },
  { title: 'State', value: 'isCanceled', align: 'center', width: '10%' },
  { title: 'Privacy', value: 'privateTournament', align: 'center', width: '10%' },
  { title: 'Start/End Time', value: 'times', align: 'center', width: '10%' },
  { title: 'Number of Questions', value: 'numberOfQuestions', align: 'center', width: '10%' },
];

const sortById = (a: Tournament, b: Tournament) => {
  if (a.id && b.id) return a.id > b.id ? 1 : -1;
  else return 0;
};

onMounted(async () => {
  store.setLoading();
  try {
    tournaments.value = await RemoteServices.getTournamentsForCourseExecution();
    tournaments.value.sort((a, b) => sortById(a, b));
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const openTournamentDashboard = async (tournament: Tournament) => {
  emit('close-show-dashboard-dialog', false);
  if (tournament)
    await router.push({
      path: '/teacher/tournament',
      query: { id: tournament.id.toString() },
    });
};
</script>

<style lang="scss" scoped></style>
