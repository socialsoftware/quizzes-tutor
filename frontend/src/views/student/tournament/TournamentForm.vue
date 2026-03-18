<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="cancelTournament()"
    @keydown.esc="cancelTournament()"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title v-if="!editMode">
        <span class="headline">
          <b>New Tournament</b>
        </span>
      </v-card-title>
      <v-card-title v-if="editMode">
        <span class="headline">
          <b>Edit Tournament</b>
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editTournament">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <b>Date:</b>
            </v-flex>
            <v-row>
              <v-col cols="12" sm="6">
                <VueDatePicker
                  id="startTimeInput"
                  v-model="newStartTime"
                  model-type="iso"
                  format="yyyy-MM-dd HH:mm"
                  placeholder="Start Time"
                ></VueDatePicker>
              </v-col>
              <v-spacer></v-spacer>
              <v-col cols="12" sm="6">
                <VueDatePicker
                  id="endTimeInput"
                  v-model="newEndTime"
                  model-type="iso"
                  format="yyyy-MM-dd HH:mm"
                  placeholder="End Time"
                ></VueDatePicker>
              </v-col>
            </v-row>
            <v-flex xs24 sm12 md8 v-if="!editMode">
              <v-row>
                <v-col cols="12" sm="4">
                  <p>
                    <b>Number Of Questions:</b>
                    {{ editTournament.numberOfQuestions }}
                  </p>
                  <v-text-field
                    min="1"
                    step="1"
                    type="number"
                    v-model="editTournament.numberOfQuestions"
                    label="Number Of Questions"
                    data-cy="NumberOfQuestions"
                  />
                </v-col>
                <v-col cols="12" sm="2">
                  <p>
                    <b>Privacy:</b>
                  </p>
                  <div
                    class="switchContainer"
                    style="
                      display: flex;
                      flex-direction: row;
                      position: relative;
                    "
                  >
                    <v-switch
                      data-cy="SwitchPrivacy"
                      v-model="editTournament.privateTournament"
                      :label="
                        editTournament.privateTournament ? 'Private' : 'Public'
                      "
                      @change="togglePrivacy()"
                    />
                  </div>
                </v-col>
                <v-col cols="12" sm="6" v-if="typePassword">
                  <p>
                    <b>Set Password:</b>
                  </p>
                  <v-text-field
                    :type="passwordFieldType"
                    v-model="password"
                    label="Password"
                    data-cy="Password"
                  >
                    <template slot="append">
                      <v-icon
                        v-if="typePassword"
                        medium
                        class="mr-2"
                        @click="switchVisibility()"
                        >visibility</v-icon
                      >
                    </template>
                  </v-text-field>
                </v-col>
              </v-row>
            </v-flex>
            <v-flex xs24 sm12 md8 v-if="editMode">
              <p>
                <b>Number Of Questions:</b>
                {{ oldNumberOfQuestions }}
              </p>
              <v-text-field
                min="1"
                step="1"
                type="number"
                v-model="editTournament.numberOfQuestions"
                label="Number Of Questions"
                data-cy="NumberOfQuestions"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-text class="text-center" v-if="editTournament">
        <v-row>
          <v-col cols="12" sm="6" class="light-green lighten-4">
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
                <h2>Currently selected</h2>
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
                {{ item.name }}
              </template>
              <template v-slot:[`item.action`]="{ item }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ props: activatorProps }">
                    <v-icon
                      small
                      class="mr-2"
                      v-bind="activatorProps"
                      @click="removeTopic(item)"
                      data-cy="removeTopic"
                    >
                      remove</v-icon
                    >
                  </template>
                  <span>Remove from Tournament</span>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-col>
          <v-col cols="12" sm="6" class="red lighten-4">
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
                <h2>Available topics</h2>
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
                {{ item.name }}
              </template>
              <template v-slot:[`item.action`]="{ item }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ props: activatorProps }">
                    <v-icon
                      small
                      class="mr-2"
                      v-bind="activatorProps"
                      @click="addTopic(item)"
                      data-cy="addTopic"
                    >
                      add</v-icon
                    >
                  </template>
                  <span>Add to Tournament</span>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
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
