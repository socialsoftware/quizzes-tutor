<template>
  <v-card class="table">
    <v-card-title>Generate questions</v-card-title>
    <v-card-subtitle>
      The questions are drafts: nothing reaches the students until you approve it in Submissions.
    </v-card-subtitle>
    <v-card-text>
      <v-row>
        <v-col cols="12" md="6">
          <v-autocomplete
            v-model="topicIds"
            :items="topicItems"
            item-title="title"
            item-value="value"
            :item-props="topicItemProps"
            label="Topics"
            :hint="topicHint"
            persistent-hint
            multiple
            chips
            closable-chips
            :no-data-text="'The course has no topics yet'"
            :menu-props="{ contentClass: 'text-left' }"
            data-cy="GenerationTopic"
          >
            <template v-slot:item="{ props: itemProps, item }">
              <v-list-item v-bind="itemProps" :style="{ paddingLeft: `${16 + (item as TopicItem).depth * 16}px` }">
                <template v-slot:append>
                  <span class="text-caption text-medium-emphasis">{{ (item as TopicItem).pieces }} piece(s)</span>
                </template>
              </v-list-item>
            </template>
          </v-autocomplete>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="focus"
            label="Focus (optional)"
            hint="What to ask about inside the topics, e.g. sign rules of the product"
            persistent-hint
            counter="500"
            :error-messages="focusError"
            data-cy="GenerationFocus"
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
            max="10"
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

      <v-row>
        <v-col cols="12" :sm="modelPick === CUSTOM ? 4 : 6">
          <v-select
            v-model="modelPick"
            :items="modelItems"
            item-title="title"
            item-value="value"
            label="Model"
            :hint="modelHint"
            persistent-hint
            :menu-props="{ contentClass: 'text-left' }"
            data-cy="GenerationModel"
          />
        </v-col>
        <template v-if="modelPick === CUSTOM">
          <v-col cols="12" sm="3">
            <v-select
              v-model="customProvider"
              :items="ownKeyProviders.map((provider) => ({ title: PROVIDER_NAMES[provider] ?? provider, value: provider }))"
              item-title="title"
              item-value="value"
              label="Provider"
              data-cy="GenerationCustomProvider"
            />
          </v-col>
          <v-col cols="12" sm="5">
            <v-text-field
              v-model="customModel"
              label="Model name"
              hint="As the provider names it, e.g. gpt-4o-mini. Your own key pays for it"
              persistent-hint
              autocomplete="off"
              data-cy="GenerationCustomModel"
            />
          </v-col>
        </template>
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
import GenerationJob from '@/models/management/generation/GenerationJob';
import { buildForest, TreeItem } from '@/services/TopicTreeEditor';
import { GenerationModels } from '@/models/management/generation/GenerationModels';
import { PROVIDER_NAMES } from '@/models/admin/LlmSettings';

interface TopicItem {
  title: string;
  value: number;
  depth: number;
  // Pieces of documents under the topic and its subtopics
  pieces: number;
  documents: number;
}

const MIN_COUNT = 1;
const MAX_COUNT = 10;
const MAX_FOCUS = 500;
const SAME_AS_MATERIALS = 'Same as the materials';
// What the Model field holds for the usual models, and for a model typed by a teacher with a key of their own
const USUAL = '';
const CUSTOM = '__custom';

const props = defineProps<{
  // Changes when topics or their pieces changed elsewhere (e.g. a document was distributed)
  topicsVersion?: number;
  // Changes when the teacher gave or removed an API key of their own
  keysVersion?: number;
}>();
const emit = defineEmits<{ (e: 'requested', job: GenerationJob): void }>();

const store = useStore();

const topics = ref<TopicNode[]>([]);
const topicIds = ref<number[]>([]);
const difficulty = ref('MEDIUM');
const count = ref(5);
const groundingMode = ref('STRICT');
const language = ref<string | null>(SAME_AS_MATERIALS);
const languageOptions = [SAME_AS_MATERIALS, 'Portuguese', 'English', 'Spanish', 'French'];
const requesting = ref(false);
const focus = ref('');
const models = ref<GenerationModels | null>(null);
const modelPick = ref<string>(USUAL);
const customProvider = ref<string | null>(null);
const customModel = ref('');

const ownKeyProviders = computed(() => models.value?.ownKeyProviders ?? []);

const nameOf = (choice: { provider: string; model: string; ownKey: boolean }) =>
  `${PROVIDER_NAMES[choice.provider] ?? choice.provider} · ${choice.model}${choice.ownKey ? ' (your key)' : ''}`;

// The usual models, the ones the administrator lists and, with a key of their own, any model of that provider
const modelItems = computed(() => {
  const items = [
    {
      title: models.value?.defaultModel ? `Usual: ${nameOf(models.value.defaultModel)}` : 'The usual model',
      value: USUAL,
    },
    ...(models.value?.models ?? []).map((choice) => ({ title: nameOf(choice), value: `${choice.provider}|${choice.model}` })),
  ];
  if (ownKeyProviders.value.length > 0) items.push({ title: 'Another model of my own key…', value: CUSTOM });
  return items;
});

const modelHint = computed(() =>
  models.value && models.value.models.length === 0 && ownKeyProviders.value.length === 0
    ? 'The administrator has not listed other models; with your own API key you can pick any of its provider'
    : 'The one that writes the questions; if it fails the usual ones take over'
);

// The model picked, as it goes in the request; null for the usual ones
const chosenModel = computed<{ provider: string; model: string } | null>(() => {
  if (modelPick.value === USUAL) return null;
  if (modelPick.value === CUSTOM) return { provider: customProvider.value ?? '', model: customModel.value.trim() };
  const [provider, ...rest] = modelPick.value.split('|');
  return { provider, model: rest.join('|') };
});

const modelValid = computed(() => modelPick.value !== CUSTOM || (!!customProvider.value && customModel.value.trim() !== ''));

const focusError = computed(() => (focus.value.length > MAX_FOCUS ? [`At most ${MAX_FOCUS} characters`] : []));

const difficultyOptions = [
  { title: 'Easy - recall a fact or definition', value: 'EASY' },
  { title: 'Medium - relate two concepts', value: 'MEDIUM' },
  { title: 'Hard - apply to a new case', value: 'HARD' },
];
const groundingOptions = [
  { title: 'Only the topics\' documents', value: 'STRICT' },
  { title: 'Documents plus general knowledge', value: 'ENRICHED' },
];

// The course's topics in tree order, each with how much text is under it and its subtopics
const topicItems = computed<TopicItem[]>(() => {
  const items: TopicItem[] = [];
  const byId = new Map(topics.value.map((topic) => [topic.id, topic]));
  const walk = (nodes: TreeItem[], depth: number) =>
    nodes.forEach((node) => {
      const topic = byId.get(node.id as number)!;
      const sources = TopicNode.subtreeSources(topics.value, topic.id);
      items.push({
        title: topic.name,
        value: topic.id,
        depth,
        pieces: sources.length,
        documents: new Set(sources.map((source) => source.materialId)).size,
      });
      walk(node.children, depth + 1);
    });
  walk(buildForest(topics.value, (topic) => topic.id, (topic) => topic.parentId, (topic) => topic.name), 0);
  return items;
});

// A topic with no text under it cannot be generated from
const topicItemProps = (item: TopicItem) => ({ disabled: item.pieces === 0 });

// The pieces under the topics picked and under their subtopics, each once (topics can share pieces)
const selectedPieces = computed(() => {
  const pieces = new Map<string, string>();
  topicIds.value.forEach((id) =>
    TopicNode.subtreeSources(topics.value, id).forEach((source) => pieces.set(source.chunkId, source.materialId))
  );
  return { pieces: pieces.size, documents: new Set(pieces.values()).size };
});

const topicHint = computed(() => {
  if (topicIds.value.length === 0)
    return 'Questions are written from the pieces of documents under the topics and their subtopics';
  const { pieces, documents } = selectedPieces.value;
  return `Written from ${pieces} piece(s) of ${documents} document(s)`;
});

const countError = computed(() =>
  Number.isInteger(count.value) && count.value >= MIN_COUNT && count.value <= MAX_COUNT
    ? []
    : [`Between ${MIN_COUNT} and ${MAX_COUNT}`]
);

const canRequest = computed(
  () =>
    topicIds.value.length > 0 &&
    selectedPieces.value.pieces > 0 &&
    countError.value.length === 0 &&
    focusError.value.length === 0 &&
    modelValid.value
);

const request = async () => {
  if (!canRequest.value) return;
  requesting.value = true;
  try {
    const job = await RemoteServices.requestGeneration({
      topicIds: [...topicIds.value],
      difficulty: difficulty.value,
      count: count.value,
      groundingMode: groundingMode.value,
      language:
        !language.value || language.value.trim() === '' || language.value === SAME_AS_MATERIALS
          ? null
          : language.value.trim(),
      focus: focus.value.trim() === '' ? null : focus.value.trim(),
      provider: chosenModel.value?.provider ?? null,
      model: chosenModel.value?.model ?? null,
    });
    emit('requested', job);
  } catch (error) {
    store.setError(error as string);
  }
  requesting.value = false;
};

const loadTopics = async () => {
  try {
    topics.value = await RemoteServices.getTopicTree();
    topicIds.value = topicIds.value.filter((id) => topics.value.some((topic) => topic.id === id));
  } catch (error) {
    store.setError(error as string);
  }
};

// What the teacher can pick depends on the keys they gave: it is read again when those change
const loadModels = async () => {
  try {
    models.value = await RemoteServices.getGenerationModels();
    if (modelPick.value === CUSTOM && models.value.ownKeyProviders.length === 0) modelPick.value = USUAL;
    if (!customProvider.value || !models.value.ownKeyProviders.includes(customProvider.value))
      customProvider.value = models.value.ownKeyProviders[0] ?? null;
    const stillThere = modelPick.value === USUAL || modelPick.value === CUSTOM || modelItems.value.some((item) => item.value === modelPick.value);
    if (!stillThere) modelPick.value = USUAL;
  } catch (error) {
    store.setError(error as string);
  }
};

watch(() => props.topicsVersion, loadTopics);
watch(() => props.keysVersion, loadModels);
onMounted(() => {
  loadTopics();
  loadModels();
});
</script>
