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
      data-cy="TournamentsList"
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
          <v-btn
            color="primary"
            dark
            @click="newTournament"
            data-cy="createButton"
            >New Tournament
          </v-btn>
        </v-card-title>
      </template>

      <template v-slot:[`item.actions`]="{ item }">
        <v-tooltip bottom v-if="item.canChange()">
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

        <v-tooltip bottom v-if="item.canJoinPublic()">
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
        <v-tooltip bottom v-if="item.canJoinPrivate()">
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
        <v-tooltip bottom v-if="item.canLeave()">
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
        <v-tooltip bottom v-if="item.canSolveQuiz()">
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
        <v-tooltip bottom v-if="item.canSeeResults()">
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

        <v-tooltip bottom v-if="item.canChange()">
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
        <v-tooltip bottom v-if="item.canChange()">
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

      <template v-slot:[`item.id`]="{ item }">
        <v-chip color="primary" small @click="openTournamentDashboard(item)">
          <span> {{ item.id }} </span>
        </v-chip>
      </template>

      <template v-slot:[`item.creator`]="{ item }">
        <v-chip small>
          <span> {{ item.creator.name }} </span>
        </v-chip>
      </template>

      <template v-slot:[`item.topics`]="{ item }">
        <view-tournament-topics :tournament="item" />
      </template>

      <template v-slot:[`item.state`]="{ item }">
        <v-chip :color="item.getStateColor()">
          {{ item.getStateName() }}
        </v-chip>
      </template>

      <template v-slot:[`item.privateTournament`]="{ item }">
        <v-chip :color="item.getPrivateColor()">
          {{ item.getPrivateName() }}
        </v-chip>
      </template>

      <template v-slot:[`item.times`]="{ item }">
        <v-chip x-small>
          {{ item.startTime }}
        </v-chip>
        <v-chip x-small>
          {{ item.endTime }}
        </v-chip>
      </template>

      <template v-slot:[`item.enrolled`]="{ item }">
        <v-chip :color="item.getEnrolledColor()">
          {{ item.getEnrolledName() }}
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
      :edit-mode="false"
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
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model="editTournamentDialog"
      :tournament="currentTournament"
      :edit-mode="true"
      v-on:edit-tournament="onEditTournament"
      v-on:close-edit-dialog="onCloseEditDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import CreateTournamentDialog from '@/views/student/tournament/TournamentForm.vue';
import EditPasswordDialog from '@/views/student/tournament/PasswordTournamentView.vue';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';
import Tournament from '@/models/user/Tournament';
import StatementManager from '@/models/statement/StatementManager';
import EditTournamentDialog from '@/views/student/tournament/TournamentForm.vue';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog,
    'edit-password-dialog': EditPasswordDialog,
    'edit-tournament-dialog': EditTournamentDialog,
    'view-tournament-topics': ViewTournamentTopics,
  },
})
export default class TournamentsListView extends Vue {
  @Prop({ type: String, required: true }) type!: string;

  tournaments: Tournament[] = [];
  currentTournament: Tournament | null = null;
  createTournamentDialog: boolean = false;
  editPasswordDialog: boolean = false;
  editTournamentDialog: boolean = false;
  search: string = '';
  password: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'actions',
      align: 'center',
      sortable: false,
      width: '40%',
    },
    {
      text: 'Tournament Number',
      value: 'id',
      align: 'center',
      width: '10%',
    },
    {
      text: 'Creator',
      value: 'creator',
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
      value: 'state',
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
    {
      text: 'Enrolled',
      value: 'enrolled',
      align: 'center',
      width: '10%',
    },
  ];

  async created() {
    await this.getTournamentsList();
  }

  @Watch('type')
  async typeChanges() {
    await this.getTournamentsList();
  }

  async getTournamentsList() {
    await this.$store.dispatch('loading');
    try {
      if (this.type === 'OPEN')
        this.tournaments = await RemoteServices.getOpenedTournamentsForCourseExecution();
      else
        this.tournaments = await RemoteServices.getClosedTournamentsForCourseExecution();
      this.tournaments.sort((a, b) => Tournament.sortById(a, b));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async openTournamentDashboard(tournament: Tournament) {
    this.$emit('close-show-dashboard-dialog', false);
    if (tournament)
      await this.$router.push({
        path: '/student/tournament',
        query: { id: tournament.id.toString() },
      });
  }

  printType() {
    if (this.type === 'OPEN') return 'Open Tournaments';
    else return 'Closed Tournaments';
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

  async onEditTournament(tournament: Tournament) {
    this.currentTournament = tournament;
    try {
      this.tournaments = await RemoteServices.getTournamentsForCourseExecution();
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

  editTournament(tournamentToEdit: Tournament) {
    this.currentTournament = tournamentToEdit;
    this.editTournamentDialog = true;
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
      await RemoteServices.joinTournament(tournamentToJoin.id, this.password);
      tournamentToJoin.enrolled = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
      return;
    }
  }

  async leaveTournament(tournamentToLeave: Tournament) {
    try {
      await RemoteServices.leaveTournament(tournamentToLeave.id);
      tournamentToLeave.enrolled = false;
    } catch (error) {
      await this.$store.dispatch('error', error);
      return;
    }
  }

  async solveQuiz(tournament: Tournament) {
    let statementManager: StatementManager = StatementManager.getInstance;
    try {
      statementManager.statementQuiz = await RemoteServices.solveTournament(
        tournament.id
      );
      await this.$router.push({ name: 'solve-quiz' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async cancelTournament(tournamentToCancel: Tournament) {
    if (confirm('Are you sure you want to cancel this tournament?')) {
      try {
        await RemoteServices.cancelTournament(tournamentToCancel.id);
        tournamentToCancel.canceled = true;
      } catch (error) {
        await this.$store.dispatch('error', error);
        return;
      }
      tournamentToCancel.canceled = true;
    }
  }

  async removeTournament(tournamentToRemove: Tournament) {
    if (confirm('Are you sure you want to delete this tournament?')) {
      try {
        if (tournamentToRemove.id)
          await RemoteServices.removeTournament(tournamentToRemove.id);
        this.tournaments = this.tournaments.filter(
          (tournament) => tournament.id !== tournamentToRemove.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
        return;
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
