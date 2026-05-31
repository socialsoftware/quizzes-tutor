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
            <v-text-field
              v-model="query.beginCreationDate"
              label="Begin Creation Date"
              variant="outlined"
              density="compact"
              clearable
            />
          </v-col>
          <v-col>
            <v-text-field
              v-model="query.endCreationDate"
              label="End Creation Date"
              variant="outlined"
              density="compact"
              clearable
            />
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
            <!-- O "0" vem antes do label, depois o slider, depois o "100" -->
            <v-range-slider
              v-model="query.difficulty"
              :max="100"
              :min="0"
              step="10"
              hide-details
              color="primary"
              track-color="#bdbdbd"
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
                <span class="slider-label">Percentage of Correct Answers</span>
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

<style lang="scss" scoped>
// Afinar a track do slider para corresponder ao exemplo de referência
:deep(.v-slider-track__background),
:deep(.v-slider-track__fill) {
  height: 2px !important;
  border-radius: 1px !important;
}

:deep(.v-slider-thumb) {
  width: 14px !important;
  height: 14px !important;
}

// Label "Percentage of Correct Answers" alinhado verticalmente dentro do prepend
.slider-label {
  font-size: 0.875rem;
  color: rgba(0, 0, 0, 0.6);
  white-space: nowrap;
  align-self: center;
  margin: 0 8px;
}

// Estilo do VueDatePicker — removido (não está ativo)
</style>
