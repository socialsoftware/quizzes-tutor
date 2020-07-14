<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
      data-cy="myTournaments"
      item-key="item.id"
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
          <v-btn to="/student/all" color="primary" dark data-cy="createButton"
            >See All Tournaments
          </v-btn>
          <v-btn
            color="primary"
            dark
            @click="newTournament"
            data-cy="createButton"
            >New Tournament
          </v-btn>
        </v-card-title>
      </template>
      <template v-slot:item.topics="{ item }">
        <view-tournament-topics :tournament="item" />
      </template>
      <template v-slot:item.times="{ item }">
        <v-chip x-small>
          {{ item.startTime }}
        </v-chip>
        <v-chip x-small>
          {{ item.endTime }}
        </v-chip>
      </template>
      <template v-slot:item.id="{ item }">
        <v-chip color="primary">
          {{ item.id }}
        </v-chip>
      </template>
      <template v-slot:item.state="{ item }">
        <v-chip :color="getStateColor(item.state)">
          {{ getStateName(item.state) }}
        </v-chip>
      </template>
      <template v-slot:item.enrolled="{ item }">
        <v-chip :color="getEnrolledColor(item.enrolled)">
          {{ getEnrolledName(item.enrolled) }}
        </v-chip>
      </template>
      <template v-slot:item.privateTournament="{ item }">
        <v-chip :color="getPrivateColor(item.privateTournament)">
          {{ getPrivateName(item.privateTournament) }}
        </v-chip>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="editTournament(item)"
              data-cy="EditTournament"
              >create</v-icon
            >
          </template>
          <span>Edit Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isNotCanceled(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="cancelTournament(item)"
              data-cy="CancelTournament"
              >cancel</v-icon
            >
          </template>
          <span>Cancel Tournament</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="removeTournament(item)"
              color="red"
              data-cy="RemoveTournament"
              >delete</v-icon
            >
          </template>
          <span>Remove Tournament</span>
        </v-tooltip>
      </template>
      <template v-slot:item.id="{ item }">
        <v-chip
          color="primary"
          small
          @click="$emit('close-show-dashboard-dialog', false)"
          :to="openTournamentDashboard(item)"
        >
          <span> {{ item.id }} </span>
        </v-chip>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on tournament's number to
      view the current ranking.
    </footer>
    <create-tournament-dialog
      v-if="currentTournament"
      v-model="createTournamentDialog"
      :tournament="currentTournament"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
    />
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model="editTournamentDialog"
      :tournament="currentTournament"
      v-on:edit-tournament="onEditTournament"
      v-on:close-edit-dialog="onCloseEditDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/user/Tournament';
import RemoteServices from '@/services/RemoteServices';
import CreateTournamentDialog from '@/views/student/tournament/CreateTournamentView.vue';
import EditTournamentDialog from '@/views/student/tournament/EditTournamentView.vue';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog,
    'edit-tournament-dialog': EditTournamentDialog,
    'view-tournament-topics': ViewTournamentTopics
  }
})
export default class MyTournamentsView extends Vue {
  tournaments: Tournament[] = [];
  currentTournament: Tournament | null = null;
  createTournamentDialog: boolean = false;
  editTournamentDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '20%'
    },
    {
      text: 'Course Acronym',
      value: 'courseAcronym',
      align: 'center',
      width: '10%'
    },
    { text: 'Tournament Number', value: 'id', align: 'center', width: '10%' },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center'
    },
    {
      text: 'State',
      value: 'state',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Privacy',
      value: 'privateTournament',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Start / End Time',
      value: 'times',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Enrolled',
      value: 'enrolled',
      align: 'center',
      sortable: false,
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getUserTournaments();
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

  openTournamentDashboard(tournament: Tournament) {
    if (tournament) return '/student/tournament?id=' + tournament.id;
  }

  newTournament() {
    this.currentTournament = new Tournament();
    this.createTournamentDialog = true;
  }

  async onCreateTournament(tournament: Tournament) {
    this.tournaments.unshift(tournament);
    this.createTournamentDialog = false;
    this.currentTournament = null;
  }

  onCloseDialog() {
    this.createTournamentDialog = false;
    this.currentTournament = null;
  }

  editTournament(tournamentToEdit: Tournament) {
    this.currentTournament = tournamentToEdit;
    this.editTournamentDialog = true;
  }

  async onEditTournament(tournament: Tournament) {
    this.currentTournament = tournament;
    try {
      this.tournaments = await RemoteServices.getUserTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.editTournamentDialog = false;
    this.currentTournament = null;
  }

  onCloseEditDialog() {
    this.editTournamentDialog = false;
    this.currentTournament = null;
  }

  getStateColor(state: string) {
    if (state === 'NOT_CANCELED') return 'green';
    else return 'red';
  }

  getStateName(state: string) {
    if (state === 'NOT_CANCELED') return 'AVAILABLE';
    else return 'CANCELLED';
  }

  getEnrolledColor(enrolled: string) {
    if (enrolled) return 'green';
    else return 'red';
  }

  getEnrolledName(enrolled: string) {
    if (enrolled) return 'YOU ARE IN';
    else return 'YOU NEED TO JOIN';
  }

  getPrivateColor(privateTournament: boolean) {
    if (privateTournament) return 'red';
    else return 'green';
  }

  getPrivateName(privateTournament: boolean) {
    if (privateTournament) return 'PRIVATE';
    else return 'PUBLIC';
  }

  isNotCanceled(tournamentToCancel: Tournament) {
    return tournamentToCancel.state === 'NOT_CANCELED';
  }
}
</script>

<style lang="scss" scoped></style>
