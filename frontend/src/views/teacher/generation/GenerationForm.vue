<template>
  <v-card class="table">
    <v-card-title>Generate questions</v-card-title>
    <v-card-subtitle>
      The questions are drafts: nothing reaches the students until you approve it in Submissions.
    </v-card-subtitle>
    <v-card-text>
      <v-row>
        <v-col cols="12" md="6">
          <v-combobox
            v-model="selectedTopic"
            :items="topics"
            item-title="name"
            label="Topic"
            hint="Pick a topic of the course or type the subject you want"
            persistent-hint
            data-cy="GenerationTopic"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-select
            v-model="selectedMaterialIds"
            :items="readyMaterials"
            item-title="filename"
            item-value="id"
            label="Materials"
            multiple
            chips
            :no-data-text="'Upload a material and wait for it to be READY'"
            data-cy="GenerationMaterials"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="3">
          <v-combobox
            v-model="language"
            :items="languageOptions"
            label="Language"
            hint="Pick one or type another"
            persistent-hint
            data-cy="GenerationLanguage"
          />
        </v-col>
        <v-col cols="12" sm="3">
          <v-select
            v-model="difficulty"
            :items="difficultyOptions"
            label="Difficulty"
            data-cy="GenerationDifficulty"
          />
        </v-col>
        <v-col cols="12" sm="3">
          <v-text-field
            v-model.number="count"
            type="number"
            min="1"
            max="20"
            label="Number of questions"
            :error-messages="countError"
            data-cy="GenerationCount"
          />
        </v-col>
        <v-col cols="12" sm="3">
          <v-select
            v-model="groundingMode"
            :items="groundingOptions"
            label="Based on"
            data-cy="GenerationMode"
          />
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn
        color="primary"
        :disabled="!canRequest"
        :loading="requesting"
        data-cy="GenerateButton"
        @click="request"
      >
        Generate
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import GenerationJob from '@/models/management/generation/GenerationJob';

const MIN_COUNT = 1;
const MAX_COUNT = 20;
const SAME_AS_MATERIALS = 'Same as the materials';

const props = defineProps<{ materials: GenerationMaterial[] }>();
const emit = defineEmits<{ (e: 'requested', job: GenerationJob): void }>();

const store = useStore();

const topics = ref<Topic[]>([]);
const selectedTopic = ref<Topic | string | null>(null);
const selectedMaterialIds = ref<string[]>([]);
const difficulty = ref('MEDIUM');
const count = ref(5);
const groundingMode = ref('STRICT');
const language = ref<string | null>(SAME_AS_MATERIALS);
const languageOptions = [SAME_AS_MATERIALS, 'Portuguese', 'English', 'Spanish', 'French'];
const requesting = ref(false);

const difficultyOptions = [
  { title: 'Easy - recall a fact or definition', value: 'EASY' },
  { title: 'Medium - relate two concepts', value: 'MEDIUM' },
  { title: 'Hard - apply to a new case', value: 'HARD' },
];
const groundingOptions = [
  { title: 'Only the selected materials', value: 'STRICT' },
  { title: 'Materials plus general knowledge', value: 'ENRICHED' },
];

const readyMaterials = computed(() => props.materials.filter((material) => material.isReady()));

// Start with every finished material ticked, but never undo a choice the teacher made
let preselected = false;
watch(
  readyMaterials,
  (ready) => {
    if (!preselected && ready.length > 0) {
      selectedMaterialIds.value = ready.map((material) => material.id);
      preselected = true;
    }
    selectedMaterialIds.value = selectedMaterialIds.value.filter((id) =>
      ready.some((material) => material.id === id)
    );
  },
  { immediate: true }
);

const topicName = computed(() => {
  const topic = selectedTopic.value;
  if (topic === null) return '';
  return (typeof topic === 'string' ? topic : topic.name).trim();
});

const countError = computed(() =>
  Number.isInteger(count.value) && count.value >= MIN_COUNT && count.value <= MAX_COUNT
    ? []
    : [`Between ${MIN_COUNT} and ${MAX_COUNT}`]
);

const canRequest = computed(
  () => topicName.value !== '' && selectedMaterialIds.value.length > 0 && countError.value.length === 0
);

const request = async () => {
  const topic = selectedTopic.value;
  requesting.value = true;
  try {
    const job = await RemoteServices.requestGeneration({
      topicId: topic !== null && typeof topic !== 'string' ? topic.id : null,
      topic: topicName.value,
      difficulty: difficulty.value,
      count: count.value,
      groundingMode: groundingMode.value,
      materialIds: selectedMaterialIds.value,
      language:
        !language.value || language.value.trim() === '' || language.value === SAME_AS_MATERIALS
          ? null
          : language.value.trim(),
    });
    emit('requested', job);
  } catch (error) {
    store.setError(error as string);
  }
  requesting.value = false;
};

onMounted(async () => {
  try {
    topics.value = await RemoteServices.getTopics();
  } catch (error) {
    store.setError(error as string);
  }
});
</script>
