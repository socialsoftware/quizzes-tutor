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
            class="text-white"
            @click="newTopic"
            data-cy="topicsNewTopicBtn"
            >New Topic</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <span data-cy="topicsGridShowButton" v-bind="props">
              <v-icon
                class="mr-2 action-button"
                @click="showQuestionsDialog(item.id)"
                >visibility</v-icon
              >
            </span>
          </template>
          <span>Show Questions</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <span data-cy="topicsGridEditButton" v-bind="props">
              <v-icon
                class="mr-2 action-button"
                @click="editTopic(item)"
                >edit</v-icon
              >
            </span>
          </template>
          <span>Edit Topic</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ props }">
            <span data-cy="topicsGridDeleteButton" v-bind="props">
              <v-icon
                class="mr-2 action-button"
                color="red"
                @click="deleteTopic(item)"
                >delete</v-icon
              >
            </span>
          </template>
          <span>Delete Topic</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.name`]="{ item }">
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
import ShowQuestionListDialog from '@/views/teacher/questions/ShowQuestionListDialog.vue';
import Question from '@/models/management/Question';

const store = useStore();

const topics = ref<Topic[]>([]);
const editedTopic = ref<Topic | null>(new Topic());
const topicDialog = ref(false);
const search = ref('');
const questionsDialog = ref(false);
const questionsToShow = ref<Question[]>([]);

const headers: any = [
  {
    title: 'Actions',
    key: 'action',
    align: 'start',
    width: '10%',
    sortable: false,
  },
  { title: 'Name', key: 'name', align: 'start' },
  {
    title: 'Questions',
    key: 'numberOfQuestions',
    align: 'center',
    width: '10%',
  },
];

onMounted(async () => {
  store.setLoading();
  try {
    topics.value = await RemoteServices.getTopics();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const customFilter = (value: any, query: string, item?: any) => {
  return (
    query != null &&
    typeof value === 'string' &&
    value.toLocaleLowerCase().indexOf(query.toLocaleLowerCase()) !== -1
  );
};

const formTitle = () => {
  return editedTopic.value === null ? 'New Topic' : 'Edit Topic';
};

const newTopic = () => {
  editedTopic.value = new Topic();
  topicDialog.value = true;
};

const closeDialogue = () => {
  topicDialog.value = false;
};

const editTopic = (topic: Topic, e?: Event) => {
  if (e) e.preventDefault();
  editedTopic.value = { ...topic };
  topicDialog.value = true;
};

const deleteTopic = async (toDeleteTopic: Topic) => {
  if (confirm('Are you sure you want to delete this topic?')) {
    try {
      await RemoteServices.deleteTopic(toDeleteTopic);
      topics.value = topics.value.filter(
        (topic) => topic.id !== toDeleteTopic.id
      );
    } catch (error) {
      store.setError(error as string);
    }
  }
};

const saveTopic = async () => {
  if (!editedTopic.value) return;
  try {
    if (editedTopic.value.id) {
      const updated = await RemoteServices.updateTopic(editedTopic.value);
      topics.value = topics.value.filter(
        (topic) => topic.id !== updated.id
      );
      editedTopic.value = updated;
    } else {
      editedTopic.value = await RemoteServices.createTopic(editedTopic.value);
    }

    topics.value.unshift(editedTopic.value);
  } catch (error) {
    store.setError(error as string);
  }
  closeDialogue();
};

const showQuestionsDialog = async (topicId: number) => {
  try {
    questionsToShow.value = await RemoteServices.getTopicQuestions(topicId);
  } catch (error) {
    store.setError(error as string);
  }
  questionsDialog.value = true;
};

const onCloseQuestionsDialog = () => {
  questionsDialog.value = false;
  questionsToShow.value = [];
};
</script>

<style lang="scss" scoped />
