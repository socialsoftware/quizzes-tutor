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
      data-cy="allTournaments"
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
          <v-btn to="/student/open" color="primary" dark data-cy="changeButton"
            >See Open Tournaments
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
      <template v-slot:item.isCanceled="{ item }">
        <v-chip :color="item.getStateColor(closedTournamentsId)">
          {{ item.getStateName(closedTournamentsId) }}
        </v-chip>
      </template>
      <template v-slot:item.enrolled="{ item }">
        <v-chip :color="item.getEnrolledColor()">
          {{ item.getEnrolledName() }}
        </v-chip>
      </template>
      <template v-slot:item.privateTournament="{ item }">
        <v-chip :color="item.getPrivateColor()">
          {{ item.getPrivateName() }}
        </v-chip>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip
          bottom
          v-if="
            item.isNotEnrolled() &&
              !item.isPrivate() &&
              !item.isClosed(closedTournamentsId)
          "
        >
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
        <v-tooltip
          bottom
          v-if="
            item.isNotEnrolled() &&
              item.isPrivate() &&
              !item.isClosed(closedTournamentsId)
          "
        >
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
        <v-tooltip
          bottom
          v-if="!item.isNotEnrolled() && !item.isClosed(closedTournamentsId)"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="leaveTournament(item)"
              data-cy="LeaveTournament"
              >fas fa-sign-out-alt</v-icon
            >
          </template>
          <span>Leave Tournament</span>
        </v-tooltip>
        <v-tooltip
          bottom
          v-if="!item.isNotEnrolled() && item.isClosed(closedTournamentsId)"
        >
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="openSolvedQuiz()"
              data-cy="SeeSolvedQuiz"
              >fas fa-file-alt</v-icon
            >
          </template>
          <span>See Solved Quiz</span>
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
      Press <v-icon class="mr-2">fas fa-file-alt</v-icon> to see tournament quiz
      answers. <v-icon class="mr-2">mouse</v-icon>Left-click on tournament's
      number to view the current ranking.
    </footer>
    <create-tournament-dialog
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
import RemoteServices from '@/services/RemoteServices';
import CreateTournamentDialog from '@/views/student/tournament/CreateTournamentView.vue';
import EditPasswordDialog from '@/views/student/tournament/PasswordTournamentView.vue';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';
import Tournament from '@/models/user/Tournament';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog,
    'edit-password-dialog': EditPasswordDialog,
    'view-tournament-topics': ViewTournamentTopics
  }
})
export default class AllTournamentView extends Vue {
  tournaments: Tournament[] = [];
  closedTournamentsId: number[] = [];
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
    {
      text: 'Tournament Number',
      value: 'id',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      width: '10%'
    },
    {
      text: 'State',
      value: 'isCanceled',
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
      this.tournaments = await RemoteServices.getAllTournamentsForCourseExecution();
      let closedTournaments = await RemoteServices.getClosedTournamentsForCourseExecution();
      closedTournaments.map(t => {
        if (t.id) this.closedTournamentsId.push(t.id);
      });
      this.tournaments.sort((a, b) => Tournament.sortById(a, b));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async openTournamentDashboard(tournament: Tournament) {
    if (tournament)
      await this.$router.push({
        name: 'tournament-participants'
      });
  }

  async openSolvedQuiz() {
    await this.$router.push({ name: 'solved-quizzes' });
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
    this.editPasswordDialog = false;
    this.currentTournament = null;
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
    const participants = tournamentToJoin.participants;
    tournamentToJoin.participants = [];
    try {
      await RemoteServices.joinTournament(tournamentToJoin, this.password);
    } catch (error) {
      await this.$store.dispatch('error', error);
      tournamentToJoin.participants = participants;
      return;
    }
    tournamentToJoin.enrolled = true;
    tournamentToJoin.participants = participants;
  }

  async leaveTournament(tournamentToLeave: Tournament) {
    const participants = tournamentToLeave.participants;
    tournamentToLeave.participants = [];
    try {
      await RemoteServices.leaveTournament(tournamentToLeave);
    } catch (error) {
      await this.$store.dispatch('error', error);
      tournamentToLeave.participants = participants;
      return;
    }
    tournamentToLeave.enrolled = false;
    tournamentToLeave.participants = participants;
  }
}
</script>

<style lang="scss" scoped></style>
