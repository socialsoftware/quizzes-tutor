<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="cancelTournament()"
    @keydown.esc="cancelTournament()"
    max-width="70%"
    max-height="95%"
  >
    <v-card class="compact-tournament-card">
      <v-card-title class="py-2 px-4" v-if="!editMode">
        <span class="headline">
          <b>New Tournament</b>
        </span>
      </v-card-title>
      <v-card-title class="py-2 px-4" v-if="editMode">
        <span class="headline">
          <b>Edit Tournament</b>
        </span>
      </v-card-title>

      <v-card-text class="text-left py-1 px-4" v-if="editTournament">
        <v-container fluid class="pa-1">
          <v-row class="my-0">
            <v-col cols="12" class="py-1">
              <b>Date:</b>
            </v-col>
          </v-row>
          <v-row class="my-0">
            <v-col cols="12" sm="6" class="py-1">
              <VueDatePicker
                id="startTimeInput"
                v-model="newStartTime"
                model-type="iso"
                format="yyyy-MM-dd HH:mm"
                placeholder="Start Time"
              ></VueDatePicker>
            </v-col>
            <v-col cols="12" sm="6" class="py-1">
              <VueDatePicker
                id="endTimeInput"
                v-model="newEndTime"
                model-type="iso"
                format="yyyy-MM-dd HH:mm"
                placeholder="End Time"
              ></VueDatePicker>
            </v-col>
          </v-row>
          <v-row v-if="!editMode" class="my-0 align-center">
            <v-col cols="12" class="d-flex align-center py-1">
              <span class="text-no-wrap mr-3"><b>Number Of Questions:</b></span>
              <v-text-field
                min="1"
                step="1"
                type="number"
                v-model="editTournament.numberOfQuestions"
                data-cy="NumberOfQuestions"
                hide-details
                density="compact"
                variant="outlined"
                style="max-width: 100px; margin-right: 40px;"
              />
              <span class="text-no-wrap mr-3"><b>Privacy:</b></span>
              <v-switch
                data-cy="SwitchPrivacy"
                v-model="editTournament.privateTournament"
                :label="
                  editTournament.privateTournament ? 'Private' : 'Public'
                "
                @change="togglePrivacy()"
                hide-details
                density="compact"
                class="mt-0"
              />
            </v-col>
          </v-row>
          <v-row v-if="!editMode && typePassword" class="my-0 align-center">
            <v-col cols="12" sm="6" class="d-flex align-center py-1">
              <span class="text-no-wrap mr-3"><b>Set Password:</b></span>
              <v-text-field
                :type="passwordFieldType"
                v-model="password"
                data-cy="Password"
                hide-details
                density="compact"
                variant="outlined"
                style="max-width: 150px;"
                :append-icon="passwordFieldType === 'password' ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append="switchVisibility()"
              />
            </v-col>
          </v-row>
          <v-row v-if="editMode" class="my-0 align-center">
            <v-col cols="12" class="d-flex align-center py-1">
              <span class="text-no-wrap mr-3">
                <b>Number Of Questions:</b>
                (Old: {{ oldNumberOfQuestions }})
              </span>
              <v-text-field
                min="1"
                step="1"
                type="number"
                v-model="editTournament.numberOfQuestions"
                data-cy="NumberOfQuestions"
                hide-details
                density="compact"
                variant="outlined"
                style="max-width: 150px;"
              />
            </v-col>
          </v-row>
        </v-container>

        <v-row class="mt-2 align-stretch justify-center">
          <v-col cols="12" sm="5" class="d-flex flex-column">
            <div class="currently-selected-list flex-grow-1">
              <v-data-table
                :headers="topicHeaders"
                :custom-filter="topicFilter"
                :items="currentTopics"
                :search="JSON.stringify(currentTopicsSearch)"
                :mobile-breakpoint="0"
                :items-per-page="5"
                :footer-props="{ itemsPerPageOptions: [5, 10, 15] }"
              >
                <template v-slot:top>
                  <h2 class="text-center w-100">Currently selected</h2>
                  <v-autocomplete
                    v-model="currentTopicsSearch"
                    label="Search"
                    :items="allTopics"
                    :filter="topicSearch"
                    v-model:search="currentTopicsSearchText"
                    @change="currentTopicsSearchText = ''"
                    item-text="name"
                    return-object
                    chips
                    small-chips
                    clearable
                    deletable-chips
                    multiple
                    dense
                    class="mx-4"
                  >
                  </v-autocomplete>
                </template>
                <template v-slot:[`item.topicsCreate`]="{ item }">
                  {{ (item as any).raw.name }}
                </template>
                <template v-slot:[`item.action`]="{ item }">
                  <v-icon
                    icon="mdi-minus"
                    class="mr-2"
                    @click="removeTopic((item as any).raw)"
                    data-cy="removeTopic"
                  >
                    <v-tooltip activator="parent" location="bottom">Remove from Tournament</v-tooltip>
                  </v-icon>
                </template>
              </v-data-table>
            </div>
          </v-col>
          <v-col cols="12" sm="5" class="d-flex flex-column">
            <div class="available-topics-list flex-grow-1">
              <v-data-table
                :headers="topicHeaders"
                :custom-filter="topicFilter"
                :items="availableTopics"
                :search="JSON.stringify(allTopicsSearch)"
                :mobile-breakpoint="0"
                :items-per-page="5"
                :footer-props="{ itemsPerPageOptions: [5, 10, 15] }"
                data-cy="Topics"
              >
                <template v-slot:top>
                  <h2 class="text-center w-100">Available topics</h2>
                  <v-autocomplete
                    v-model="allTopicsSearch"
                    label="Search"
                    :items="allTopics"
                    :filter="topicSearch"
                    v-model:search="allTopicsSearchText"
                    @change="allTopicsSearchText = ''"
                    item-text="name"
                    return-object
                    chips
                    small-chips
                    clearable
                    deletable-chips
                    multiple
                    dense
                    class="mx-4"
                  >
                  </v-autocomplete>
                </template>
                <template v-slot:[`item.topicsCreate`]="{ item }">
                  {{ (item as any).raw.name }}
                </template>
                <template v-slot:[`item.action`]="{ item }">
                  <v-icon
                    icon="mdi-plus"
                    class="mr-2"
                    @click="addTopic((item as any).raw)"
                    data-cy="addTopic"
                  >
                    <v-tooltip activator="parent" location="bottom">Add to Tournament</v-tooltip>
                  </v-icon>
                </template>
              </v-data-table>
            </div>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions class="px-6 py-2">
        <v-spacer />
        <v-btn color="primary" @click="cancelTournament" data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="primary" @click="saveTournament" data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/user/Tournament';
import Topic from '@/models/management/Topic';
import { VueDatePicker } from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

const props = defineProps<{
  dialog: boolean;
  tournament: Tournament;
  editMode: boolean;
}>();

const emit = defineEmits([
  'close-edit-dialog',
  'close-dialog',
  'new-tournament',
  'edit-tournament'
]);

const store = useStore();

const editTournament = ref<Tournament>(new Tournament());
const currentTopicsSearch = ref('');
const currentTopicsSearchText = ref('');
const allTopicsSearch = ref('');
const allTopicsSearchText = ref('');

const allTopics = ref<Topic[]>([]);
const currentTopics = ref<Topic[]>([]);
const availableTopics = ref<Topic[]>([]);

const oldStartTime = ref('');
const oldEndTime = ref('');
const oldNumberOfQuestions = ref(-1);
const oldTopics = ref<String[]>([]);

const newStartTime = ref('');
const newEndTime = ref('');

const typePassword = ref(false);
const passwordFieldType = ref('password');
const password = ref('');

const topicsId = ref<Number[]>([]);

const topicHeaders: any = [
  {
    title: 'Topics',
    key: 'topicsCreate',
    align: 'start',
    sortable: false,
  },
  {
    title: 'Actions',
    key: 'action',
    align: 'center',
    width: '150px',
    sortable: false,
  },
];

const storeOldValues = async () => {
  if (editTournament.value.startTime) {
    oldStartTime.value = newStartTime.value = editTournament.value.startTime;
  }
  if (editTournament.value.endTime) {
    oldEndTime.value = newEndTime.value = editTournament.value.endTime;
  }
  if (editTournament.value.numberOfQuestions) {
    oldNumberOfQuestions.value = editTournament.value.numberOfQuestions;
  }
  oldTopics.value = editTournament.value.topics!;
};

const updateCurrentTopics = async () => {
  editTournament.value.topics!.forEach((topicName) => {
    availableTopics.value.forEach((topic) => {
      if (topic.name.valueOf() === topicName.valueOf()) {
        addTopic(topic);
      }
    });
  });
};

onMounted(async () => {
  editTournament.value = props.editMode
    ? props.tournament
    : new Tournament(props.tournament);

  if (props.editMode) {
    await storeOldValues();
  }

  store.setLoading();
  try {
    allTopics.value = await RemoteServices.getAvailableTopicsByCourseExecution();
    availableTopics.value = allTopics.value;
    if (props.editMode && editTournament.value.topics !== undefined) {
      await updateCurrentTopics();
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const resetChanges = async () => {
  editTournament.value.startTime = oldStartTime.value;
  editTournament.value.endTime = oldEndTime.value;
  editTournament.value.numberOfQuestions = oldNumberOfQuestions.value;
  editTournament.value.topics = oldTopics.value;
};

const cancelTournament = async () => {
  if (props.editMode) {
    await resetChanges();
    emit('close-edit-dialog');
  } else {
    emit('close-dialog');
  }
};

const saveTournament = async () => {
  editTournament.value.startTime = newStartTime.value;
  editTournament.value.endTime = newEndTime.value;

  if (
    editTournament.value &&
    (!editTournament.value.startTime ||
      !editTournament.value.endTime ||
      !editTournament.value.numberOfQuestions ||
      currentTopics.value.length == 0)
  ) {
    store.setError('Tournament must have Start Time, End Time, Number Of Questions and Topics');
    if (props.editMode) {
      await resetChanges();
    }
    return;
  }

  if (
    !props.editMode &&
    editTournament.value &&
    editTournament.value.privateTournament &&
    password.value === ''
  ) {
    store.setError('Tournament must have a password in order to be private');
    return;
  }

  if (!props.editMode && editTournament.value && editTournament.value.id == null) {
    editTournament.value.canceled = false;
    editTournament.value.password = password.value;

    topicsId.value = currentTopics.value.map((topic) => topic.id as Number);

    try {
      const result = await RemoteServices.createTournament(
        topicsId.value,
        editTournament.value
      );
      emit('new-tournament', result);
    } catch (error) {
      store.setError(error as string);
    }
  }

  if (props.editMode && editTournament.value && editTournament.value.id != null) {
    let topicsList = currentTopics.value.map((topic) => topic.id as number);

    try {
      const result = await RemoteServices.updateTournament(
        topicsList,
        editTournament.value
      );
      emit('edit-tournament', result);
    } catch (error) {
      store.setError(error as string);
    }
    editTournament.value.topics = currentTopics.value.map((topic) => topic.name);
  }
};

const togglePrivacy = async () => {
  props.tournament.privateTournament = !props.tournament.privateTournament;
  typePassword.value = !typePassword.value;
  password.value = '';
};

const switchVisibility = async () => {
  passwordFieldType.value = passwordFieldType.value === 'password' ? 'text' : 'password';
};

const topicFilter = (value: any, search: string, item?: any) => {
  if (!item || !item.raw) return false;
  let topic: Topic = item.raw;
  let searchTopics = JSON.parse(search);
  if (searchTopics !== '') {
    return searchTopics
      .map((searchTopic: Topic) => searchTopic.name)
      .every((t: string) => topic.name.includes(t));
  }
  return true;
};

const topicSearch = (value: any, search: string, item?: any) => {
  if (!item || !item.raw) return false;
  let topic: Topic = item.raw;
  return (
    search != null &&
    topic.name.toLowerCase().indexOf(search.toLowerCase()) !== -1
  );
};

const removeTopic = (topic: Topic) => {
  availableTopics.value.push(topic);
  availableTopics.value.sort((a, b) => {
    let result = a.name.localeCompare(b.name);
    return result === 0 ? 0 : result > 0 ? 1 : -1;
  });
  currentTopics.value = currentTopics.value.filter((t) => t.id != topic.id);
};

const addTopic = (topic: Topic) => {
  currentTopics.value.push(topic);
  currentTopics.value.sort((a, b) => {
    let result = a.name.localeCompare(b.name);
    return result === 0 ? 0 : result > 0 ? 1 : -1;
  });
  availableTopics.value = availableTopics.value.filter((t) => t.id != topic.id);
};
</script>

<style scoped>
:deep(.dp__main) {
  width: 100%;
}

.compact-tournament-card {
  padding-top: 12px !important;
  padding-bottom: 12px !important;
}

.currently-selected-list {
  border: 2px solid #a5d6a7 !important;
  background-color: #e8f5e9 !important;
  border-radius: 8px;
  padding: 12px;
}

.available-topics-list {
  border: 2px solid #ffcdd2 !important;
  background-color: #ffebee !important;
  border-radius: 8px;
  padding: 12px;
}
</style>
