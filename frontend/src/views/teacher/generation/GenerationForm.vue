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
          <v-text-field
            v-model="focus"
            label="Focus (optional)"
            hint="What to ask about inside the topic, e.g. sign rules of the product"
            persistent-hint
            counter="500"
            :error-messages="focusError"
            data-cy="GenerationFocus"
          />
        </v-col>
      </v-row>

      <v-row v-if="topicSections.length > 0">
        <v-col cols="12">
          <v-radio-group v-model="sourceMode" inline hide-details data-cy="GenerationSourceMode">
            <v-radio :label="topicSectionsLabel" value="topic" data-cy="SourceTopic" />
            <v-radio label="Choose materials and sections myself" value="manual" data-cy="SourceManual" />
          </v-radio-group>
        </v-col>
      </v-row>

      <v-row v-if="sourceMode === 'manual'">
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
        <v-col cols="12" md="6">
          <v-autocomplete
            v-model="selectedSections"
            :items="sections"
            item-title="title"
            item-value="path"
            label="Sections (optional)"
            hint="Nothing selected: the whole documents are searched for the topic"
            persistent-hint
            multiple
            chips
            closable-chips
            clearable
            :loading="loadingSections"
            :disabled="selectedMaterialIds.length === 0"
            :no-data-text="'No sections found in the selected materials'"
            data-cy="GenerationSections"
          >
            <template v-slot:item="{ props: itemProps, item }">
              <v-list-item v-bind="itemProps" :subtitle="(item as GenerationSection).parentPath" />
            </template>
          </v-autocomplete>
          <div class="mt-1">
            <v-btn
              size="x-small"
              variant="text"
              :disabled="sections.length === 0"
              data-cy="SelectAllSections"
              @click="selectAllSections"
            >
              Select all
            </v-btn>
            <v-btn
              size="x-small"
              variant="text"
              :disabled="selectedSections.length === 0"
              data-cy="ClearSections"
              @click="clearSections"
            >
              Clear
            </v-btn>
          </div>
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
import TopicNode from '@/models/management/TopicNode';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import GenerationJob from '@/models/management/generation/GenerationJob';
import GenerationSection from '@/models/management/generation/GenerationSection';

const MIN_COUNT = 1;
const MAX_COUNT = 20;
const MAX_FOCUS = 500;
const SAME_AS_MATERIALS = 'Same as the materials';

const props = defineProps<{
  materials: GenerationMaterial[];
  // Changes when topics were created elsewhere (e.g. from a document), so the list is read again
  topicsVersion?: number;
}>();
const emit = defineEmits<{ (e: 'requested', job: GenerationJob): void }>();

const store = useStore();

const topics = ref<TopicNode[]>([]);
const selectedTopic = ref<TopicNode | string | null>(null);
const selectedMaterialIds = ref<string[]>([]);
const difficulty = ref('MEDIUM');
const count = ref(5);
const groundingMode = ref('STRICT');
const language = ref<string | null>(SAME_AS_MATERIALS);
const languageOptions = [SAME_AS_MATERIALS, 'Portuguese', 'English', 'Spanish', 'French'];
const requesting = ref(false);
const sections = ref<GenerationSection[]>([]);
const selectedSections = ref<string[]>([]);
const loadingSections = ref(false);
const focus = ref('');
// 'topic': read the document sections linked to the topic; 'manual': the materials and sections picked here
const sourceMode = ref<'topic' | 'manual'>('manual');

const focusError = computed(() => (focus.value.length > MAX_FOCUS ? [`At most ${MAX_FOCUS} characters`] : []));

// The sections offered are those of the materials currently selected, in reading order
const loadSections = async (materialIds: string[]) => {
  loadingSections.value = true;
  try {
    const perMaterial = await Promise.all(materialIds.map((id) => RemoteServices.getGenerationSections(id)));
    const seen = new Set<string>();
    sections.value = perMaterial.flat().filter((section) => !seen.has(section.path) && !!seen.add(section.path));
    selectedSections.value = selectedSections.value.filter((path) => seen.has(path));
  } catch (error) {
    store.setError(error as string);
  }
  loadingSections.value = false;
};

const selectAllSections = () => {
  selectedSections.value = sections.value.map((section) => section.path);
};

const clearSections = () => {
  selectedSections.value = [];
};

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

// The topic picked from the course's tree; a subject typed by hand has none
const topicNode = computed(() => {
  const topic = selectedTopic.value;
  return topic !== null && typeof topic !== 'string' ? topic : null;
});

const topicName = computed(() => {
  const topic = selectedTopic.value;
  if (topic === null) return '';
  return (typeof topic === 'string' ? topic : topic.name).trim();
});

// The sections linked to the topic and to its subtopics
const topicSections = computed(() => (topicNode.value ? TopicNode.subtreeSources(topics.value, topicNode.value.id) : []));

const topicSectionsLabel = computed(() => {
  const documents = new Set(topicSections.value.map((source) => source.materialId)).size;
  const parts = topicSections.value.length;
  return `Use the topic's sections (${parts} section${parts === 1 ? '' : 's'} from ${documents} document${documents === 1 ? '' : 's'})`;
});

// A topic with sections is read from them by default; the teacher can still choose by hand
watch(
  () => topicSections.value.length > 0,
  (hasSections) => {
    sourceMode.value = hasSections ? 'topic' : 'manual';
  },
  { immediate: true }
);

const countError = computed(() =>
  Number.isInteger(count.value) && count.value >= MIN_COUNT && count.value <= MAX_COUNT
    ? []
    : [`Between ${MIN_COUNT} and ${MAX_COUNT}`]
);

watch(selectedMaterialIds, (ids) => loadSections(ids), { deep: true, immediate: true });

const readsFromTopic = computed(() => sourceMode.value === 'topic' && topicSections.value.length > 0);

const canRequest = computed(
  () =>
    topicName.value !== '' &&
    (readsFromTopic.value || selectedMaterialIds.value.length > 0) &&
    countError.value.length === 0 &&
    focusError.value.length === 0
);

// Every section ticked asks for the same as none ticked, so the list is not sent
const sectionsToSend = computed(() =>
  sections.value.length > 0 && selectedSections.value.length === sections.value.length ? [] : selectedSections.value
);

const request = async () => {
  requesting.value = true;
  try {
    const fromTopic = readsFromTopic.value;
    const job = await RemoteServices.requestGeneration({
      topicId: topicNode.value ? topicNode.value.id : null,
      topic: topicName.value,
      difficulty: difficulty.value,
      count: count.value,
      groundingMode: groundingMode.value,
      materialIds: fromTopic ? [] : selectedMaterialIds.value,
      fromTopic,
      language:
        !language.value || language.value.trim() === '' || language.value === SAME_AS_MATERIALS
          ? null
          : language.value.trim(),
      sections: fromTopic ? [] : sectionsToSend.value,
      focus: focus.value.trim() === '' ? null : focus.value.trim(),
    });
    emit('requested', job);
  } catch (error) {
    store.setError(error as string);
  }
  requesting.value = false;
};

const loadTopics = async () => {
  try {
    const picked = topicNode.value?.id;
    topics.value = await RemoteServices.getTopicTree();
    // Keep the picked topic, now with its up-to-date sections
    if (picked !== undefined) selectedTopic.value = topics.value.find((topic) => topic.id === picked) ?? null;
  } catch (error) {
    store.setError(error as string);
  }
};

watch(() => props.topicsVersion, loadTopics);
onMounted(loadTopics);
</script>
