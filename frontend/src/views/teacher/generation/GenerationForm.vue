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
            v-model="topicId"
            :items="topicItems"
            item-title="title"
            item-value="value"
            :item-props="topicItemProps"
            label="Topic"
            :hint="topicHint"
            persistent-hint
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
            hint="What to ask about inside the topic, e.g. sign rules of the product"
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
import GenerationJob from '@/models/management/generation/GenerationJob';
import { buildForest, TreeItem } from '@/services/TopicTreeEditor';

interface TopicItem {
  title: string;
  value: number;
  depth: number;
  // Pieces of documents under the topic and its subtopics
  pieces: number;
  documents: number;
}

const MIN_COUNT = 1;
const MAX_COUNT = 20;
const MAX_FOCUS = 500;
const SAME_AS_MATERIALS = 'Same as the materials';

const props = defineProps<{
  // Changes when topics or their pieces changed elsewhere (e.g. a document was distributed)
  topicsVersion?: number;
}>();
const emit = defineEmits<{ (e: 'requested', job: GenerationJob): void }>();

const store = useStore();

const topics = ref<TopicNode[]>([]);
const topicId = ref<number | null>(null);
const difficulty = ref('MEDIUM');
const count = ref(5);
const groundingMode = ref('STRICT');
const language = ref<string | null>(SAME_AS_MATERIALS);
const languageOptions = [SAME_AS_MATERIALS, 'Portuguese', 'English', 'Spanish', 'French'];
const requesting = ref(false);
const focus = ref('');

const focusError = computed(() => (focus.value.length > MAX_FOCUS ? [`At most ${MAX_FOCUS} characters`] : []));

const difficultyOptions = [
  { title: 'Easy - recall a fact or definition', value: 'EASY' },
  { title: 'Medium - relate two concepts', value: 'MEDIUM' },
  { title: 'Hard - apply to a new case', value: 'HARD' },
];
const groundingOptions = [
  { title: 'Only the topic\'s documents', value: 'STRICT' },
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

const selectedItem = computed(() => topicItems.value.find((item) => item.value === topicId.value) ?? null);

const topicHint = computed(() => {
  const item = selectedItem.value;
  if (!item) return 'Questions are written from the pieces of documents under the topic and its subtopics';
  return `Written from ${item.pieces} piece(s) of ${item.documents} document(s)`;
});

const countError = computed(() =>
  Number.isInteger(count.value) && count.value >= MIN_COUNT && count.value <= MAX_COUNT
    ? []
    : [`Between ${MIN_COUNT} and ${MAX_COUNT}`]
);

const canRequest = computed(
  () =>
    selectedItem.value !== null &&
    selectedItem.value.pieces > 0 &&
    countError.value.length === 0 &&
    focusError.value.length === 0
);

const request = async () => {
  if (!canRequest.value) return;
  requesting.value = true;
  try {
    const job = await RemoteServices.requestGeneration({
      topicId: topicId.value!,
      difficulty: difficulty.value,
      count: count.value,
      groundingMode: groundingMode.value,
      language:
        !language.value || language.value.trim() === '' || language.value === SAME_AS_MATERIALS
          ? null
          : language.value.trim(),
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
    topics.value = await RemoteServices.getTopicTree();
    if (topicId.value !== null && !topics.value.some((topic) => topic.id === topicId.value)) topicId.value = null;
  } catch (error) {
    store.setError(error as string);
  }
};

watch(() => props.topicsVersion, loadTopics);
onMounted(loadTopics);
</script>
