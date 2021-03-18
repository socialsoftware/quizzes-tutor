<template v-if="topics">
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="topics"
      :search="search"
      :mobile-breakpoint="0"
      :items-per-page="50"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      data-cy="topicsGrid"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            data-cy="Search"
            single-line
            hide-details
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="newTopic"
            data-cy="topicsNewTopicBtn"
            >New Topic</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="showQuestionsDialog(item.id)"
              >visibility</v-icon
            >
          </template>
          <span>Show Questions</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="editTopic(item)"
              data-cy="topicsGridEditButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Topic</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              v-on="on"
              @click="deleteTopic(item)"
              color="red"
              data-cy="topicsGridDeleteButton"
              >delete</v-icon
            >
          </template>
          <span>Delete Topic</span>
        </v-tooltip>
      </template>
      <template
        v-slot:[`item.name`]="{ item }"
        style="background: rebeccapurple"
      >
        <div
          @click="showQuestionsDialog(item.id)"
          @contextmenu="editTopic(item, $event)"
          class="clickableTitle"
        >
          {{ item.name }}
        </div>
      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2 action-button">mouse</v-icon>Right-click on topic's
      name to edit it.
    </footer>

    <v-dialog v-model="topicDialog" max-width="75%">
      <v-card data-cy="topicsCreateOrEditDialog">
        <v-card-title>
          <span class="headline">{{ formTitle() }}</span>
        </v-card-title>

        <v-card-text v-if="editedTopic">
          <v-text-field
            v-model="editedTopic.name"
            label="Topic"
            data-cy="topicsFormTopicNameInput"
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="red darken-1" @click="closeDialogue">Cancel</v-btn>
          <v-btn color="green darken-1" @click="saveTopic">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <show-question-list-dialog
      :dialog="questionsDialog"
      :questions="questionsToShow"
      v-on:close="onCloseQuestionsDialog"
    ></show-question-list-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
import ShowQuestionListDialog from '@/views/teacher/questions/ShowQuestionListDialog.vue';
import Question from '@/models/management/Question';

@Component({
  components: { ShowQuestionListDialog },
})
export default class TopicsView extends Vue {
  topics: Topic[] = [];
  editedTopic: Topic = new Topic();
  topicDialog: boolean = false;
  search: string = '';
  questionsDialog: boolean = false;
  questionsToShow: Question[] = [];
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5px',
      sortable: false,
    },
    { text: 'Name', value: 'name', align: 'left' },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '115px',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      typeof value === 'string' &&
      value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1
    );
  }

  formTitle() {
    return this.editedTopic === null ? 'New Topic' : 'Edit Topic';
  }

  newTopic() {
    this.editedTopic = new Topic();
    this.topicDialog = true;
  }

  closeDialogue() {
    this.topicDialog = false;
  }

  editTopic(topic: Topic, e?: Event) {
    if (e) e.preventDefault();
    this.editedTopic = { ...topic };
    this.topicDialog = true;
  }

  async deleteTopic(toDeleteTopic: Topic) {
    if (confirm('Are you sure you want to delete this topic?')) {
      try {
        await RemoteServices.deleteTopic(toDeleteTopic);
        this.topics = this.topics.filter(
          (topic) => topic.id !== toDeleteTopic.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async saveTopic() {
    try {
      if (this.editedTopic.id) {
        this.editedTopic = await RemoteServices.updateTopic(this.editedTopic);
        this.topics = this.topics.filter(
          (topic) => topic.id !== this.editedTopic.id
        );
      } else if (this.editedTopic) {
        this.editedTopic = await RemoteServices.createTopic(this.editedTopic);
      }

      this.topics.unshift(this.editedTopic);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.closeDialogue();
  }

  async showQuestionsDialog(topicId: number) {
    try {
      this.questionsToShow = await RemoteServices.getTopicQuestions(topicId);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.questionsDialog = true;
  }

  onCloseQuestionsDialog() {
    this.questionsDialog = false;
    this.questionsToShow = [];
  }
}
</script>

<style lang="scss" scoped />
