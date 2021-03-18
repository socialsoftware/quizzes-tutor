<template>
  <v-dialog
    :value="dialog"
    @input="cancelTournament()"
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
                <VueCtkDateTimePicker
                  label="Start Time"
                  id="startTimeInput"
                  v-model="newStartTime"
                  format="YYYY-MM-DDTHH:mm:ssZ"
                >
                </VueCtkDateTimePicker>
              </v-col>
              <v-spacer></v-spacer>
              <v-col cols="12" sm="6">
                <VueCtkDateTimePicker
                  label="End Time"
                  id="endTimeInput"
                  v-model="newEndTime"
                  format="YYYY-MM-DDTHH:mm:ssZ"
                >
                </VueCtkDateTimePicker>
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
                <v-col cols="12" sm="6" v-if="this.typePassword">
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
                        v-if="this.typePassword"
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
                  :search-input.sync="currentTopicsSearchText"
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
                  <template v-slot:activator="{ on }">
                    <v-icon
                      small
                      class="mr-2"
                      v-on="on"
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
                  :search-input.sync="allTopicsSearchText"
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
                  <template v-slot:activator="{ on }">
                    <v-icon
                      small
                      class="mr-2"
                      v-on="on"
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

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/user/Tournament';
import Topic from '@/models/management/Topic';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component
export default class TournamentForm extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;
  @Prop({ type: Boolean, required: true }) readonly editMode!: boolean;

  editTournament!: Tournament;
  currentTopicsSearch: string = '';
  currentTopicsSearchText: string = '';
  allTopicsSearch: string = '';
  allTopicsSearchText: string = '';

  allTopics: Topic[] = [];
  currentTopics: Topic[] = [];
  availableTopics: Topic[] = [];

  oldStartTime: string = '';
  oldEndTime: string = '';
  oldNumberOfQuestions: number = -1;
  oldTopics: String[] = [];

  newStartTime: string = '';
  newEndTime: string = '';

  typePassword: boolean = false;
  passwordFieldType: string = 'password';
  password: string = '';

  topicsId: Number[] = [];

  topicHeaders: object = [
    {
      text: 'Topics',
      value: 'topicsCreate',
      align: 'left',
      sortable: false,
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '150px',
      sortable: false,
    },
  ];

  async created() {
    this.editTournament = this.editMode
      ? this.tournament
      : new Tournament(this.tournament);

    if (this.editMode) {
      await this.storeOldValues();
    }

    await this.$store.dispatch('loading');
    try {
      this.allTopics = await RemoteServices.getAvailableTopicsByCourseExecution();
      this.availableTopics = this.allTopics;
      if (this.editMode && this.editTournament.topics !== undefined) {
        await this.updateCurrentTopics();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async storeOldValues() {
    if (this.editTournament.startTime) {
      this.oldStartTime = this.newStartTime = this.editTournament.startTime;
    }
    if (this.editTournament.endTime) {
      this.oldEndTime = this.newEndTime = this.editTournament.endTime;
    }
    if (this.editTournament.numberOfQuestions) {
      this.oldNumberOfQuestions = this.editTournament.numberOfQuestions;
    }
    this.oldTopics = this.editTournament.topics!;
  }

  async updateCurrentTopics() {
    this.editTournament.topics!.forEach((topicName) => {
      this.availableTopics!.forEach((topic) => {
        if (topic.name.valueOf() === topicName.valueOf()) {
          this.addTopic(topic);
        }
      });
    });
  }

  async resetChanges() {
    this.editTournament.startTime = this.oldStartTime;
    this.editTournament.endTime = this.oldEndTime;
    this.editTournament.numberOfQuestions = this.oldNumberOfQuestions;
    this.editTournament.topics = this.oldTopics;
  }

  async cancelTournament() {
    if (this.editMode) {
      await this.resetChanges();
      this.$emit('close-edit-dialog');
    } else {
      this.$emit('close-dialog');
    }
  }

  async saveTournament() {
    this.editTournament.startTime = this.newStartTime;
    this.editTournament.endTime = this.newEndTime;

    if (
      this.editTournament &&
      (!this.editTournament.startTime ||
        !this.editTournament.endTime ||
        !this.editTournament.numberOfQuestions ||
        this.currentTopics.length == 0)
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament must have Start Time, End Time, Number Of Questions and Topics'
      );
      if (this.editMode) {
        await this.resetChanges();
      }
      return;
    }

    if (
      !this.editMode &&
      this.editTournament &&
      this.editTournament.privateTournament &&
      this.password === ''
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament must have a password in order to be private'
      );
      return;
    }

    if (
      !this.editMode &&
      this.editTournament &&
      this.editTournament.id == null
    ) {
      this.editTournament.canceled = false;
      this.editTournament.password = this.password;

      this.topicsId = this.currentTopics.map((topic) => {
        return topic.id;
      });

      try {
        const result = await RemoteServices.createTournament(
          this.topicsId,
          this.editTournament
        );
        this.$emit('new-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    if (
      this.editMode &&
      this.editTournament &&
      this.editTournament.id != null
    ) {
      let topicsList = this.currentTopics.map((topic) => {
        return topic.id;
      });

      try {
        const result = await RemoteServices.updateTournament(
          topicsList,
          this.editTournament
        );
        this.$emit('edit-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      this.editTournament.topics = this.currentTopics.map((topic) => {
        return topic.name;
      });
    }
  }

  async togglePrivacy() {
    this.tournament.privateTournament = !this.tournament.privateTournament;
    this.typePassword = !this.typePassword;
    this.password = '';
  }

  async switchVisibility() {
    this.passwordFieldType =
      this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  topicFilter(value: string, search: string, topic: Topic) {
    let searchTopics = JSON.parse(search);

    if (searchTopics !== '') {
      return searchTopics
        .map((searchTopic: Topic) => searchTopic.name)
        .every((t: string) => topic.name.includes(t));
    }
    return true;
  }

  topicSearch(topic: Topic, search: string) {
    return (
      search != null &&
      topic.name.toLowerCase().indexOf(search.toLowerCase()) !== -1
    );
  }

  removeTopic(topic: Topic) {
    this.availableTopics.push(topic);
    this.availableTopics.sort((a, b) => {
      let result = a.name.localeCompare(b.name);
      return result === 0 ? 0 : result > 0 ? 1 : -1;
    });
    this.currentTopics = this.currentTopics.filter((t) => t.id != topic.id);
  }

  addTopic(topic: Topic) {
    this.currentTopics.push(topic);
    this.currentTopics.sort((a, b) => {
      let result = a.name.localeCompare(b.name);
      return result === 0 ? 0 : result > 0 ? 1 : -1;
    });
    this.availableTopics = this.availableTopics.filter((t) => t.id != topic.id);
  }
}
</script>
