<template>
  <v-card v-if="editMode && assessment" class="table">
    <v-card-title>
      <span>Create Assessment</span>
      <v-spacer />
      <v-btn color="primary" class="text-white" @click="$emit('switchMode')">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn color="green darken-1" class="text-white" @click="saveAssessment">Save</v-btn>
    </v-card-title>
    <v-card-text>
      <v-container fluid>
        <v-row>
          <v-col>
            <v-text-field
              v-model="assessment.title"
              label="Title"
            ></v-text-field>
          </v-col>

          <v-col>
            <v-text-field
              min="0"
              step="1"
              type="number"
              label="Order"
              :model-value="assessment.sequence"
              @update:model-value="assessment.sequence = Number($event)"
            ></v-text-field>
          </v-col>
        </v-row>
        <div class="topics-flex-container">
          <div class="currently-selected-list">
            <v-data-table
              :headers="topicHeaders"
              :custom-filter="topicFilter"
              :items="assessment.topicConjunctions"
              :search="JSON.stringify(currentTopicsSearch) || ''"
              :mobile-breakpoint="0"
              :items-per-page="15"
              :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
            >
              <template v-slot:top>
                <h2 class="text-center w-100">Currently selected</h2>
                <v-autocomplete
                  v-model="currentTopicsSearch"
                  label="Search"
                  :items="allTopics"
                  :custom-filter="topicSearch"
                  :search="currentTopicsSearchText"
                  @update:search="currentTopicsSearchText = $event"
                  @update:model-value="currentTopicsSearchText = ''"
                  item-title="name"
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
              <template v-slot:[`item.topics`]="{ item }">
                <div v-if="item.topics.length > 0">
                  <v-chip v-for="topic in item.topics" :key="topic.id">
                    {{ topic.name }}
                  </v-chip>
                </div>
                <div v-else>No Topic</div>
              </template>
              <template v-slot:[`item.action`]="{ item }">
                <!-- Vazio de acordo com o pedido -->
              </template>
            </v-data-table>
          </div>
          <div class="available-topics-list">
            <v-data-table
              :headers="topicHeaders"
              :custom-filter="topicFilter"
              :items="topicConjunctions"
              :search="JSON.stringify(allTopicsSearch) || ''"
              :mobile-breakpoint="0"
              :items-per-page="15"
              :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
              data-cy="Topics"
            >
              <template v-slot:top>
                <h2 class="text-center w-100">Available topics</h2>
                <v-autocomplete
                  v-model="allTopicsSearch"
                  label="Search"
                  :items="allTopics"
                  :custom-filter="topicSearch"
                  :search="allTopicsSearchText"
                  @update:search="allTopicsSearchText = $event"
                  @update:model-value="allTopicsSearchText = ''"
                  item-title="name"
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
              <template v-slot:[`item.topics`]="{ item }">
                <div v-if="item.topics && item.topics.length > 0">
                  <v-chip v-for="topic in item.topics" :key="topic.id">
                    {{ topic.name }}
                  </v-chip>
                </div>
                <div v-else>No Topic</div>
              </template>
              <template v-slot:[`item.action`]="{ item }">
                <div class="d-flex" style="gap: 8px;">
                  <v-btn
                    density="compact"
                    variant="flat"
                    color="grey-lighten-3"
                    icon
                    @click="addTopicConjunction((item as any).raw)"
                  >
                    <v-icon>mdi-chevron-left</v-icon>
                    <v-tooltip activator="parent" location="bottom">Add to Assessment</v-tooltip>
                  </v-btn>
                  <v-btn
                    density="compact"
                    variant="flat"
                    color="grey-lighten-3"
                    icon
                    @click="showQuestionsDialog((item as any).raw)"
                  >
                    <v-icon>mdi-eye</v-icon>
                    <v-tooltip activator="parent" location="bottom">Show Questions</v-tooltip>
                  </v-btn>
                </div>
              </template>
            </v-data-table>
          </div>
        </div>
      </v-container>
    </v-card-text>

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
import Assessment from '@/models/management/Assessment';
import Question from '@/models/management/Question';
import { convertMarkDown as convertMarkDownService } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import TopicConjunction from '@/models/management/TopicConjunction';
import Topic from '@/models/management/Topic';
import ShowQuestionListDialog from '@/views/teacher/questions/ShowQuestionListDialog.vue';

const props = defineProps<{
  assessment: Assessment;
  editMode: boolean;
}>();

const emit = defineEmits(['switchMode', 'updateAssessment']);
const store = useStore();

const currentTopicsSearch = ref<any>('');
const currentTopicsSearchText = ref('');
const allTopicsSearch = ref<any>('');
const allTopicsSearchText = ref('');

const questionsDialog = ref(false);
const allTopics = ref<Topic[]>([]);
const topicConjunctions = ref<TopicConjunction[]>([]);
const questionsToShow = ref<Question[]>([]);

const topicHeaders = [
  {
    title: 'Actions',
    key: 'action',
    align: 'start',
    width: '10%',
    sortable: false,
  },
  {
    title: 'Topics',
    key: 'topics',
    align: 'start',
    sortable: false,
  },
] as any;

onMounted(async () => {
  store.setLoading();
  try {
    const [fetchedTopicConjunctions, fetchedAllTopics] = await Promise.all([
      RemoteServices.getTopicConjunctions(
        props.assessment.id ? props.assessment.id : 0
      ),
      RemoteServices.getTopics(),
    ]);
    topicConjunctions.value = fetchedTopicConjunctions;
    allTopics.value = fetchedAllTopics;

    let assessmentTopicConjunctionIds = props.assessment.topicConjunctions.map(
      (topicConjunction: TopicConjunction) => {
        return topicConjunction.id;
      }
    );
    topicConjunctions.value = topicConjunctions.value.filter(
      (topicConjunction) =>
        !assessmentTopicConjunctionIds.includes(topicConjunction.id)
    );
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const topicFilter = (
  value: any,
  search: string,
  topicConjunction?: any
) => {
  if (!topicConjunction || !topicConjunction.raw) return false;
  let searchTopics = [];
  try {
    searchTopics = JSON.parse(search);
  } catch (e) {
    searchTopics = [];
  }

  if (searchTopics && searchTopics.length > 0) {
    return searchTopics
      .map((searchTopic: Topic) => searchTopic.name)
      .every((t: string) =>
        topicConjunction.raw.topics.map((topic: Topic) => topic.name).includes(t)
      );
  }
  return true;
};

const topicSearch = (value: any, search: string, item?: any) => {
  if (!item || !item.raw) return false;
  return (
    search != null &&
    item.raw.name.toLowerCase().indexOf(search.toLowerCase()) !== -1
  );
};

const saveAssessment = async () => {
  if (props.assessment && !props.assessment.title) {
    store.setError('Assessment must have title');
    return;
  }

  store.setLoading();
  try {
    let updatedAssessment: Assessment = await RemoteServices.saveAssessment(
      props.assessment
    );
    emit('updateAssessment', updatedAssessment);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const showQuestionsDialog = async (topicConjunction: TopicConjunction) => {
  store.setLoading();
  try {
    questionsToShow.value = await RemoteServices.getTopicConjuctionQuestions(
      topicConjunction
    );
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();

  questionsDialog.value = true;
};

const onCloseQuestionsDialog = () => {
  questionsDialog.value = false;
  questionsToShow.value = [];
};

const removeTopicConjunction = (topicConjuntion: TopicConjunction) => {
  topicConjunctions.value.push(topicConjuntion);
  props.assessment.topicConjunctions =
    props.assessment.topicConjunctions.filter(
      (tc) => tc.sequence != topicConjuntion.sequence
    );
};

const addTopicConjunction = (topicConjuntion: TopicConjunction) => {
  props.assessment.topicConjunctions.push(topicConjuntion);
  topicConjunctions.value = topicConjunctions.value.filter(
    (tc) => tc.sequence !== topicConjuntion.sequence
  );
};

const convertMarkDown = (text: string, image: Image | null = null): string => {
  return convertMarkDownService(text, image);
};
</script>

<style lang="scss" scoped>
.topics-flex-container {
  display: flex;
  gap: 20px;
  width: 100%;
  align-items: stretch;
}

.currently-selected-list {
  flex: 1;
  border: 2px solid #a5d6a7 !important;
  background-color: #e8f5e9 !important;
  border-radius: 8px;
  padding: 12px;
}

.available-topics-list {
  flex: 1;
  border: 2px solid #ffcdd2 !important;
  background-color: #ffebee !important;
  border-radius: 8px;
  padding: 12px;
}
</style>
