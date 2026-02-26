<template>
  <v-card>
    <v-card-title>
      <span class="text-subtitle-1">Query Questions Form</span>
    </v-card-title>

    <v-card-text class="text-left">
      <v-container fluid>
        <v-row>
          <v-col>
            <v-text-field v-model="query.content" label="Content"
          /></v-col>
          <v-col>
            <VueCtkDateTimePicker
              label="Begin Creation Date"
              id="beginCreationDate"
              v-model="query.beginCreationDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
            ></VueCtkDateTimePicker>
          </v-col>
          <v-col>
            <VueCtkDateTimePicker
              label="End Creation Date"
              id="endCreationDate"
              v-model="query.endCreationDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
            ></VueCtkDateTimePicker>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-select
              v-model="query.topics"
              :items="topics"
              attach
              chips
              item-title="name"
              item-value="id"
              label="Topics"
              multiple
            ></v-select>
          </v-col>
          <v-col v-show="!availableOnly">
            <v-select
              v-model="query.status"
              :items="status"
              attach
              chips
              label="Status"
              multiple
            ></v-select>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-checkbox
              v-model="query.clarificationsOnly"
              label="Clarifications Only"
              hide-details
            ></v-checkbox>
            <v-checkbox
              v-model="query.noAnswersOnly"
              label="No Answers Only"
              hide-details
            ></v-checkbox>
          </v-col>
          <v-col>
            <v-range-slider
              v-model="query.difficulty"
              :max="100"
              :min="0"
              step="10"
              hide-details
              label="Percentage of Correct Answers"
              ><template v-slot:prepend>
                <v-text-field
                  :model-value="query.difficulty[0]"
                  class="mt-0 pt-0"
                  hide-details
                  single-line
                  type="number"
                  style="width: 40px"
                  @update:model-value="$set(query.difficulty, 0, $event)"
                ></v-text-field>
              </template>
              <template v-slot:append>
                <v-text-field
                  :model-value="query.difficulty[1]"
                  class="mt-0 pt-0"
                  hide-details
                  single-line
                  type="number"
                  style="width: 50px"
                  @update:model-value="$set(query.difficulty, 1, $event)"
                ></v-text-field> </template
            ></v-range-slider>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>

    <v-card-actions>
      <v-spacer />
      <v-btn
        class="text-white"
        color="green darken-1"
        @click="queryQuestions"
        data-cy="submitQueryButton"
        >Submit Query</v-btn
      >
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
import QuestionQuery from '@/models/management/QuestionQuery';

const props = defineProps<{
  availableOnly: boolean;
}>();

const emit = defineEmits(['query-questions']);
const store = useStore();

const topics = ref<Topic[]>([]);
const status = ref<string[]>(['AVAILABLE', 'DISABLED', 'REMOVED']);
const query = ref<QuestionQuery>(new QuestionQuery());

onMounted(async () => {
  if (props.availableOnly) {
    query.value.status = ['AVAILABLE'];
  }

  store.setLoading();
  try {
    topics.value = await RemoteServices.getTopics();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const $set = (arr: any[], index: number, value: any) => {
  arr[index] = value;
};

const queryQuestions = async () => {
  store.setLoading();
  try {
    let questions = await RemoteServices.getQuestionsByQuery(query.value);
    emit('query-questions', questions);
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>
