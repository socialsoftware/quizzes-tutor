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
          <v-btn to="/student/all" color="primary" dark data-cy="changeButton"
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
      <template v-slot:item.id="{ item }">
        <v-chip color="primary" small @click="openTournamentDashboard(item)">
          <span> {{ item.id }} </span>
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
        <v-tooltip bottom v-if="item.isNotCanceled()">
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
  closedTournamentsId: number[] = [];
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
      this.tournaments = await RemoteServices.getTournamentsByUserId();
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
    this.$emit('close-show-dashboard-dialog', false);
    if (tournament)
      await this.$router.push({
        name: 'tournament-participants'
      });
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
      this.tournaments = await RemoteServices.getTournamentsByUserId();
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

  async cancelTournament(tournamentToCancel: Tournament) {
    if (confirm('Are you sure you want to cancel this tournament?')) {
      const participants = tournamentToCancel.participants;
      tournamentToCancel.participants = [];
      try {
        await RemoteServices.cancelTournament(tournamentToCancel.id);
        this.tournaments = await RemoteServices.getTournamentsByUserId();
      } catch (error) {
        await this.$store.dispatch('error', error);
        tournamentToCancel.participants = participants;
        return;
      }
      tournamentToCancel.canceled = true;
      tournamentToCancel.participants = participants;
    }
  }

  async removeTournament(tournamentToRemove: Tournament) {
    if (confirm('Are you sure you want to delete this tournament?')) {
      const participants = tournamentToRemove.participants;
      tournamentToRemove.participants = [];
      try {
        if (tournamentToRemove.id)
          await RemoteServices.removeTournament(tournamentToRemove.id);
        this.tournaments = await RemoteServices.getTournamentsByUserId();
      } catch (error) {
        await this.$store.dispatch('error', error);
        tournamentToRemove.participants = participants;
        return;
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
