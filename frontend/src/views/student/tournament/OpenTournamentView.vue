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
      data-cy="openTournaments"
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
        <v-tooltip bottom v-if="!isNotEnrolled(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="solveQuiz(item)"
              data-cy="SolveQuiz"
              >fa-file-signature</v-icon
            >
          </template>
          <span>Solve Quiz</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isNotEnrolled(item) && !isPrivate(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="joinPublicTournament(item)"
              data-cy="JoinTournament"
              >fa-sign-in-alt</v-icon
            >
          </template>
          <span>Join Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isNotEnrolled(item) && isPrivate(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="openPasswordDialog(item)"
              data-cy="JoinTournament"
              >fa-sign-in-alt</v-icon
            >
          </template>
          <span>Join Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="!isNotEnrolled(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="leaveTournament(item)"
              data-cy="LeaveTournament"
              >fa-sign-out-alt</v-icon
            >
          </template>
          <span>Leave Tournament</span>
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
      Press <v-icon class="mr-2">fa-file-signature</v-icon> to solve tournament
      quiz.

      <v-icon class="mr-2">mouse</v-icon>Left-click on tournament's number to
      view the current ranking.
    </footer>
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model="createTournamentDialog"
      :tournament="currentTournament"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
    />
    <edit-password-dialog
      v-if="currentTournament"
      v-model="editPasswordDialog"
      :tournament="currentTournament"
      v-on:enter-password="joinPrivateTournament"
      v-on:close-password-dialog="onClosePasswordDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/user/Tournament';
import RemoteServices from '@/services/RemoteServices';
import CreateTournamentDialog from '@/views/student/tournament/CreateTournamentView.vue';
import EditPasswordDialog from '@/views/student/tournament/PasswordTournamentView.vue';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';

@Component({
  components: {
    'edit-tournament-dialog': CreateTournamentDialog,
    'edit-password-dialog': EditPasswordDialog,
    'view-tournament-topics': ViewTournamentTopics
  }
})
export default class OpenTournamentView extends Vue {
  tournaments: Tournament[] = [];
  currentTournament: Tournament | null = null;
  createTournamentDialog: boolean = false;
  editPasswordDialog: boolean = false;
  search: string = '';
  password: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
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
      align: 'center',
      width: '10%'
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
      text: 'Start/End Time',
      value: 'times',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
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
      this.tournaments = await RemoteServices.getOpenTournaments();
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

  openPasswordDialog(tournamentToJoin: Tournament) {
    this.currentTournament = tournamentToJoin;
    this.editPasswordDialog = true;
  }

  onClosePasswordDialog() {
    this.currentTournament = null;
    this.editPasswordDialog = false;
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

  isNotEnrolled(tournamentToJoin: Tournament) {
    return !tournamentToJoin.enrolled;
  }

  isPrivate(tournamentToJoin: Tournament) {
    return tournamentToJoin.privateTournament;
  }

  async joinPrivateTournament(password: string) {
    this.password = password;
    if (this.currentTournament)
      await this.joinPublicTournament(this.currentTournament);
    this.editPasswordDialog = false;
    this.currentTournament = null;
    this.password = '';
  }

  async joinPublicTournament(tournamentToJoin: Tournament) {
    try {
      await RemoteServices.joinTournament(tournamentToJoin, this.password);
    } catch (error) {
      await this.$store.dispatch('error', error);
      return;
    }
  }
}
</script>

<style lang="scss" scoped></style>
