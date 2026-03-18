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
            class="text-white"
            @click="newTournament"
            data-cy="createButton"
            >New Tournament
          </v-btn>
        </v-card-title>
      </template>

      <template v-slot:[`item.actions`]="{ item }">
        <v-tooltip bottom v-if="item.canChange()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="editTournament(item)"
              data-cy="EditTournament"
              >create</v-icon
            >
          </template>
          <span>Edit Tournament</span>
        </v-tooltip>

        <v-tooltip bottom v-if="item.canJoinPublic()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="joinPublicTournament(item)"
              data-cy="JoinTournament"
              >fa-sign-in-alt</v-icon
            >
          </template>
          <span>Join Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.canJoinPrivate()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="openPasswordDialog(item)"
              data-cy="JoinTournament"
              >fa-sign-in-alt</v-icon
            >
          </template>
          <span>Join Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.canLeave()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="leaveTournament(item)"
              data-cy="LeaveTournament"
              >fas fa-sign-out-alt</v-icon
            >
          </template>
          <span>Leave Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.canSolveQuiz()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="solveQuiz(item)"
              data-cy="SolveQuiz"
              >fa-file-signature</v-icon
            >
          </template>
          <span>Solve Quiz</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.canSeeResults()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="openSolvedQuiz()"
              data-cy="SeeSolvedQuiz"
              >fas fa-file-alt</v-icon
            >
          </template>
          <span>See Solved Quiz</span>
        </v-tooltip>

        <v-tooltip bottom v-if="item.canChange()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
              @click="cancelTournament(item)"
              data-cy="CancelTournament"
              >cancel</v-icon
            >
          </template>
          <span>Cancel Tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.canChange()">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              large
              class="mr-2"
              v-bind="activatorProps"
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
        <v-chip color="primary" class="text-white" size="small" @click="openTournamentDashboard(item)">
          <span> {{ item.id }} </span>
        </v-chip>
      </template>

      <template v-slot:[`item.creator`]="{ item }">
        <v-chip size="small">
          <span> {{ item.creator.name }} </span>
        </v-chip>
      </template>

      <template v-slot:[`item.topics`]="{ item }">
        <view-tournament-topics :tournament="item" />
      </template>

      <template v-slot:[`item.state`]="{ item }">
        <v-chip :color="item.getStateColor()" class="text-white">
          {{ item.getStateName() }}
        </v-chip>
      </template>

      <template v-slot:[`item.privateTournament`]="{ item }">
        <v-chip :color="item.getPrivateColor()" class="text-white">
          {{ item.getPrivateName() }}
        </v-chip>
      </template>

      <template v-slot:[`item.times`]="{ item }">
        <v-chip size="x-small">
          {{ item.startTime }}
        </v-chip>
        <v-chip size="x-small">
          {{ item.endTime }}
        </v-chip>
      </template>

      <template v-slot:[`item.enrolled`]="{ item }">
        <v-chip :color="item.getEnrolledColor()" class="text-white">
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
      v-model:dialog="createTournamentDialog"
      :tournament="currentTournament"
      :edit-mode="false"
      @new-tournament="onCreateTournament"
      @close-dialog="onCloseDialog"
    />
    <edit-password-dialog
      v-if="currentTournament"
      v-model:dialog="editPasswordDialog"
      :tournament="currentTournament"
      @enter-password="joinPrivateTournament"
      @close-password-dialog="onClosePasswordDialog"
    />
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model:dialog="editTournamentDialog"
      :tournament="currentTournament"
      :edit-mode="true"
      @edit-tournament="onEditTournament"
      @close-edit-dialog="onCloseEditDialog"
    />
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import RemoteServices from '@/services/RemoteServices';
import CreateTournamentDialog from '@/views/student/tournament/TournamentForm.vue';
import EditTournamentDialog from '@/views/student/tournament/TournamentForm.vue';
import EditPasswordDialog from '@/views/student/tournament/PasswordTournamentView.vue';
import ViewTournamentTopics from '@/views/student/tournament/ViewTournamentTopics.vue';
import Tournament from '@/models/user/Tournament';
import StatementQuiz from '@/models/statement/StatementQuiz';

const props = defineProps<{ type: string }>();

const emit = defineEmits(['close-show-dashboard-dialog']);

const store = useStore();
const router = useRouter();

const tournaments = ref<Tournament[]>([]);
const currentTournament = ref<Tournament | null>(null);
const createTournamentDialog = ref(false);
const editPasswordDialog = ref(false);
const editTournamentDialog = ref(false);
const search = ref('');
const password = ref('');
const headers: any = [
  {
    title: 'Actions',
    key: 'actions',
    align: 'center',
    sortable: false,
    width: '40%',
  },
  {
    title: 'Tournament Number',
    key: 'id',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Creator',
    key: 'creator',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Topics',
    key: 'topics',
    align: 'center',
    width: '10%',
  },
  {
    title: 'State',
    key: 'state',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Privacy',
    key: 'privateTournament',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Start/End Time',
    key: 'times',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Number of Questions',
    key: 'numberOfQuestions',
    align: 'center',
    width: '10%',
  },
  {
    title: 'Enrolled',
    key: 'enrolled',
    align: 'center',
    width: '10%',
  },
];

const getTournamentsList = async () => {
  store.setLoading();
  try {
    if (props.type === 'OPEN') {
      tournaments.value = await RemoteServices.getOpenedTournamentsForCourseExecution();
    } else {
      tournaments.value = await RemoteServices.getClosedTournamentsForCourseExecution();
    }
    tournaments.value.sort((a, b) => Tournament.sortById(a, b));
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

onMounted(() => {
  getTournamentsList();
});

watch(() => props.type, () => {
  getTournamentsList();
});

const openTournamentDashboard = async (tournament: Tournament) => {
  emit('close-show-dashboard-dialog', false);
  if (tournament) {
    await router.push({
      path: '/student/tournament',
      query: { id: tournament.id.toString() },
    });
  }
};

const printType = () => {
  if (props.type === 'OPEN') return 'Open Tournaments';
  else return 'Closed Tournaments';
};

const openSolvedQuiz = async () => {
  await router.push({ name: 'solved-quizzes' });
};

const newTournament = () => {
  currentTournament.value = new Tournament();
  createTournamentDialog.value = true;
};

const onCreateTournament = async (tournament: Tournament) => {
  tournaments.value.unshift(tournament);
  createTournamentDialog.value = false;
  currentTournament.value = null;
};

const onCloseDialog = () => {
  createTournamentDialog.value = false;
  currentTournament.value = null;
};

const openPasswordDialog = (tournamentToJoin: Tournament) => {
  currentTournament.value = tournamentToJoin;
  editPasswordDialog.value = true;
};

const onClosePasswordDialog = () => {
  editPasswordDialog.value = false;
  currentTournament.value = null;
};

const onEditTournament = async (tournament: Tournament) => {
  currentTournament.value = tournament;
  try {
    tournaments.value = await RemoteServices.getTournamentsForCourseExecution();
  } catch (error) {
    store.setError(error as string);
  }
  editTournamentDialog.value = false;
  currentTournament.value = null;
};

const onCloseEditDialog = () => {
  editTournamentDialog.value = false;
  currentTournament.value = null;
};

const editTournament = (tournamentToEdit: Tournament) => {
  currentTournament.value = tournamentToEdit;
  editTournamentDialog.value = true;
};

const joinPrivateTournament = async (pwd: string) => {
  password.value = pwd;
  if (currentTournament.value) {
    await joinPublicTournament(currentTournament.value);
  }
  editPasswordDialog.value = false;
  currentTournament.value = null;
  password.value = '';
};

const joinPublicTournament = async (tournamentToJoin: Tournament) => {
  try {
    await RemoteServices.joinTournament(tournamentToJoin.id, password.value);
    tournamentToJoin.enrolled = true;
  } catch (error) {
    store.setError(error as string);
    return;
  }
};

const leaveTournament = async (tournamentToLeave: Tournament) => {
  try {
    await RemoteServices.leaveTournament(tournamentToLeave.id);
    tournamentToLeave.enrolled = false;
  } catch (error) {
    store.setError(error as string);
    return;
  }
};

const solveQuiz = async (tournament: Tournament) => {
  store.setLoading();

  let statementQuiz: StatementQuiz;
  try {
    statementQuiz = await RemoteServices.solveTournament(tournament.id);
    await store.setStatementQuiz(statementQuiz);
    await router.push({ name: 'solve-quiz' });
  } catch (error) {
    store.setError(error as string);
  }

  store.clearLoading();
};

const cancelTournament = async (tournamentToCancel: Tournament) => {
  if (confirm('Are you sure you want to cancel this tournament?')) {
    try {
      await RemoteServices.cancelTournament(tournamentToCancel.id);
      tournamentToCancel.canceled = true;
    } catch (error) {
      store.setError(error as string);
      return;
    }
  }
};

const removeTournament = async (tournamentToRemove: Tournament) => {
  if (confirm('Are you sure you want to delete this tournament?')) {
    try {
      if (tournamentToRemove.id) {
        await RemoteServices.removeTournament(tournamentToRemove.id);
      }
      tournaments.value = tournaments.value.filter(
        (tournament) => tournament.id !== tournamentToRemove.id
      );
    } catch (error) {
      store.setError(error as string);
      return;
    }
  }
};
</script>

<style lang="scss" scoped></style>
