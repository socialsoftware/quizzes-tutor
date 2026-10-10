<template>
  <v-dialog :model-value="dialog" fullscreen persistent @update:model-value="close">
    <v-card class="distribution" data-cy="DistributeMaterialDialog">
      <v-toolbar color="primary" density="comfortable">
        <v-toolbar-title>Put "{{ materialName }}" under topics</v-toolbar-title>
        <v-spacer />
        <v-menu>
          <template v-slot:activator="{ props: menuProps }">
            <v-btn variant="text" v-bind="menuProps" :disabled="loading || chunks.length === 0" data-cy="SuggestFromHeadings">
              Suggest from headings
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="option in LEVEL_OPTIONS"
              :key="option.levels"
              :title="option.title"
              :data-cy="`SuggestLevels${option.levels}`"
              @click="applySuggestion(option.levels)"
            />
          </v-list>
        </v-menu>
        <v-btn variant="text" :disabled="saving" data-cy="CloseDistribution" @click="close">Close</v-btn>
        <v-btn
          variant="elevated"
          :loading="saving"
          :disabled="loading || problems.size > 0"
          data-cy="SaveDistribution"
          @click="save"
        >
          Save
        </v-btn>
      </v-toolbar>

      <v-card-text v-if="!loading">
        <v-alert v-if="suggested" type="info" variant="tonal" density="compact" class="mb-3" data-cy="SuggestedNotice">
          Suggested from the headings of the document. Check it, change what is wrong and save.
        </v-alert>
        <div class="d-flex align-center flex-wrap mb-3">
          <span class="text-medium-emphasis" data-cy="DistributionSummary">
            {{ placedCount }} of {{ chunks.length }} pieces are under a topic. Pieces under no topic are not used to
            generate questions. A topic chosen for a heading also goes to the headings below it.
          </span>
          <v-spacer />
          <v-btn size="small" variant="text" data-cy="ExpandAll" @click="expandAll(!allExpanded)">
            {{ allExpanded ? 'Collapse all' : 'Show all pieces' }}
          </v-btn>
        </div>

        <v-row>
          <v-col cols="12" md="8" class="document">
            <div v-for="group in groups" :key="group.index" data-cy="ChunkGroup">
              <div class="d-flex align-center group-row" :style="{ paddingLeft: `${Math.max(group.depth - 1, 0) * 20}px` }">
                <v-btn
                  variant="text"
                  size="x-small"
                  :icon="expanded.has(group.index) ? 'fas fa-chevron-down' : 'fas fa-chevron-right'"
                  data-cy="ToggleGroup"
                  @click="toggle(group.index)"
                />
                <span class="font-weight-medium group-title" data-cy="GroupTitle">{{ group.title }}</span>
                <span class="text-caption text-medium-emphasis ml-2 text-no-wrap">{{ group.chunkIds.length }} piece(s)</span>
                <v-spacer />
                <v-chip
                  size="small"
                  class="ml-2 destination"
                  :color="colorOf(groupDestination(group))"
                  data-cy="GroupDestination"
                  @click="openPicker({ group: group.index })"
                >
                  {{ groupLabel(group) }}
                </v-chip>
              </div>
              <div v-if="expanded.has(group.index)" :style="{ paddingLeft: `${Math.max(group.depth - 1, 0) * 20 + 32}px` }">
                <div v-for="id in group.chunkIds" :key="id" class="d-flex align-start chunk-row" data-cy="ChunkRow">
                  <div class="chunk-text">
                    {{ fullText.has(id) ? chunkById.get(id)!.text : preview(chunkById.get(id)!.text) }}
                    <a
                      v-if="chunkById.get(id)!.text.length > PREVIEW_CHARS"
                      class="text-primary more"
                      data-cy="MoreText"
                      @click="toggleText(id)"
                      >{{ fullText.has(id) ? 'less' : 'more' }}</a
                    >
                  </div>
                  <v-chip
                    size="x-small"
                    class="ml-2 destination"
                    :color="colorOf(destinations[id])"
                    data-cy="ChunkDestination"
                    @click="openPicker({ chunk: id })"
                  >
                    {{ labelOf(destinations[id]) }}
                  </v-chip>
                </div>
              </div>
            </div>
          </v-col>

          <v-col cols="12" md="4">
            <v-card variant="outlined" class="topics-panel">
              <v-card-title class="d-flex align-center text-subtitle-1">
                Topics
                <v-spacer />
                <v-btn size="small" variant="text" data-cy="AddTopic" @click="askNewTopic">New topic</v-btn>
              </v-card-title>
              <v-list density="compact" data-cy="DistributionTopics">
                <v-list-item v-if="options.length === 0" class="text-medium-emphasis">The course has no topics yet.</v-list-item>
                <v-list-item
                  v-for="option in options"
                  :key="option.value"
                  :style="{ paddingLeft: `${16 + option.depth * 16}px` }"
                  data-cy="DistributionTopic"
                >
                  <span :class="{ 'text-medium-emphasis': !countOf(option.value) }">{{ option.title }}</span>
                  <v-chip v-if="option.isNew" size="x-small" color="primary" class="ml-1">new</v-chip>
                  <v-chip v-if="countOf(option.value)" size="x-small" class="ml-1">{{ countOf(option.value) }}</v-chip>
                  <div v-if="problemOf(option)" class="text-error text-caption" data-cy="TopicProblem">
                    {{ problemOf(option) }}
                  </div>
                  <template v-if="option.isNew" v-slot:append>
                    <v-btn icon="edit" size="x-small" variant="text" title="Rename" data-cy="RenameNewTopic" @click="askRename(option)" />
                    <v-btn
                      icon="delete"
                      size="x-small"
                      variant="text"
                      color="red"
                      title="Remove"
                      data-cy="RemoveNewTopic"
                      @click="removeTopic(option)"
                    />
                  </template>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-text v-else class="text-center py-12">
        <v-progress-circular indeterminate color="primary" />
      </v-card-text>
    </v-card>

    <v-dialog v-model="pickerOpen" max-width="560">
      <v-card class="distribution" data-cy="TopicPicker">
        <v-card-title>{{ pickerTitle }}</v-card-title>
        <v-card-text>
          <p v-if="pickerBelow > 0" class="text-caption text-medium-emphasis mb-2">
            Also for the {{ pickerBelow }} heading(s) below it.
          </p>
          <v-autocomplete
            v-model="pickerValue"
            :items="pickerItems"
            item-title="title"
            item-value="value"
            label="Topic"
            autofocus
            :menu-props="{ contentClass: 'text-left' }"
            data-cy="PickerTopic"
          >
            <template v-slot:item="{ props: itemProps, item }">
              <v-list-item v-bind="itemProps" :style="{ paddingLeft: `${16 + (item as PickerItem).depth * 16}px` }" />
            </template>
          </v-autocomplete>
          <div class="d-flex align-center new-topic">
            <v-text-field
              v-model="pickerNewName"
              label="Or create a new topic under the one above"
              :placeholder="pickerSuggestedName"
              persistent-placeholder
              data-cy="PickerNewName"
              @keyup.enter="applyNewTopic"
            />
            <v-btn variant="tonal" class="ml-2" data-cy="PickerCreate" :disabled="newTopicName === ''" @click="applyNewTopic">
              Create
            </v-btn>
          </div>
        </v-card-text>
        <v-card-actions class="flex-wrap">
          <v-spacer />
          <v-btn variant="text" @click="pickerOpen = false">Cancel</v-btn>
          <v-btn color="primary" data-cy="PickerApply" @click="applyPicked">Use topic</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="nameOpen" max-width="420">
      <v-card class="distribution">
        <v-card-title>{{ renaming ? 'Rename new topic' : 'New topic' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="name" label="Name" autofocus data-cy="NewTopicName" @keyup.enter="saveName" />
          <v-autocomplete
            v-if="!renaming"
            v-model="nameParent"
            :items="parentItems"
            item-title="title"
            item-value="value"
            label="Under"
            :menu-props="{ contentClass: 'text-left' }"
            data-cy="NewTopicParent"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="nameOpen = false">Cancel</v-btn>
          <v-btn color="primary" :disabled="name.trim() === ''" data-cy="SaveNewTopic" @click="saveName">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import MaterialChunk from '@/models/management/generation/MaterialChunk';
import TopicNode from '@/models/management/TopicNode';
import {
  ChunkGroup,
  created,
  Destination,
  DestinationOption,
  destinationOptions,
  existing,
  groupAndBelow,
  groupChunks,
  nameProblems,
  NewTopic,
  removeNewTopic,
  suggest,
  toTree,
  UNUSED,
} from '@/services/ChunkDistribution';

interface PickerItem {
  title: string;
  value: Destination;
  depth: number;
}

const PREVIEW_CHARS = 240;
const DEFAULT_LEVELS = 2;
const LEVEL_OPTIONS = [
  { levels: 1, title: 'One topic per chapter (first level of headings)' },
  { levels: 2, title: 'Chapters and their sections (two levels)' },
  { levels: 3, title: 'Three levels of headings' },
  { levels: 99, title: 'Every heading' },
];
const MIXED = '\u0000mixed';

const props = defineProps<{
  dialog: boolean;
  materialId: string;
  materialName: string;
}>();

const emit = defineEmits<{
  (e: 'update:dialog', value: boolean): void;
  (e: 'saved'): void;
}>();

const store = useStore();

const loading = ref(true);
const saving = ref(false);
const chunks = ref<MaterialChunk[]>([]);
const topics = ref<TopicNode[]>([]);
const destinations = ref<Record<string, Destination>>({});
const newTopics = ref<NewTopic[]>([]);
const dirty = ref(false);
const suggested = ref(false);
const expanded = ref(new Set<number>());
const fullText = ref(new Set<string>());
let newTopicCounter = 0;

const groups = computed(() => groupChunks(chunks.value));
const chunkById = computed(() => new Map(chunks.value.map((chunk) => [chunk.id, chunk])));
const options = computed(() => destinationOptions(topics.value, newTopics.value));
const optionByValue = computed(() => new Map(options.value.map((option) => [option.value, option])));
const problems = computed(() => nameProblems(newTopics.value, topics.value));
const placedCount = computed(() => chunks.value.filter((chunk) => (destinations.value[chunk.id] ?? UNUSED) !== UNUSED).length);
const allExpanded = computed(() => groups.value.length > 0 && expanded.value.size === groups.value.length);

const counts = computed(() => {
  const result = new Map<Destination, number>();
  Object.values(destinations.value).forEach((destination) => result.set(destination, (result.get(destination) ?? 0) + 1));
  return result;
});
const countOf = (destination: Destination) => (destination === UNUSED ? 0 : counts.value.get(destination) ?? 0);

const labelOf = (destination: Destination | undefined) =>
  !destination ? 'No topic' : optionByValue.value.get(destination)?.title ?? 'No topic';

const colorOf = (destination: Destination) => (destination === UNUSED || destination === MIXED ? undefined : 'primary');

const groupDestination = (group: ChunkGroup): Destination => {
  const values = new Set(group.chunkIds.map((id) => destinations.value[id] ?? UNUSED));
  return values.size === 1 ? [...values][0] : MIXED;
};

const groupLabel = (group: ChunkGroup) => {
  const destination = groupDestination(group);
  return destination === MIXED ? 'Several topics' : labelOf(destination);
};

const problemOf = (option: DestinationOption) =>
  option.isNew ? problems.value.get(option.value.slice(2)) ?? null : null;

const preview = (text: string) => (text.length > PREVIEW_CHARS ? `${text.slice(0, PREVIEW_CHARS)}…` : text);

const toggle = (index: number) => {
  const next = new Set(expanded.value);
  if (next.has(index)) next.delete(index);
  else next.add(index);
  expanded.value = next;
};

const expandAll = (open: boolean) => {
  expanded.value = open ? new Set(groups.value.map((group) => group.index)) : new Set();
};

const toggleText = (id: string) => {
  const next = new Set(fullText.value);
  if (next.has(id)) next.delete(id);
  else next.add(id);
  fullText.value = next;
};

const load = async () => {
  loading.value = true;
  try {
    const [loadedChunks, loadedTopics] = await Promise.all([
      RemoteServices.getMaterialChunks(props.materialId),
      RemoteServices.getTopicTree(),
    ]);
    chunks.value = loadedChunks;
    topics.value = loadedTopics;
    newTopics.value = [];
    destinations.value = Object.fromEntries(
      loadedChunks.map((chunk) => [chunk.id, chunk.topicId === null ? UNUSED : existing(chunk.topicId)])
    );
    dirty.value = false;
    suggested.value = false;
    // A document no topic has yet starts from the suggestion, so the teacher only fixes it
    if (loadedChunks.length > 0 && loadedChunks.every((chunk) => chunk.topicId === null)) applySuggestion(DEFAULT_LEVELS, false);
  } catch (error) {
    store.setError(error as string);
    emit('update:dialog', false);
  }
  loading.value = false;
};

const applySuggestion = (levels: number, ask = true) => {
  if (ask && dirty.value && !confirm('Replace what you changed with a new suggestion?')) return;
  const suggestion = suggest(chunks.value, topics.value, levels);
  destinations.value = suggestion.destinations;
  newTopics.value = suggestion.newTopics;
  dirty.value = true;
  suggested.value = true;
};

// Topic picker, for one piece or for a heading (with the headings below it)
const pickerOpen = ref(false);
const pickerTarget = ref<{ group?: number; chunk?: string }>({});
const pickerValue = ref<Destination>(UNUSED);
const pickerNewName = ref('');

const pickerItems = computed<PickerItem[]>(() => [
  { title: 'No topic (not used for questions)', value: UNUSED, depth: 0 },
  ...options.value.map((option) => ({ title: option.isNew ? `${option.title} (new)` : option.title, value: option.value, depth: option.depth })),
]);

const pickerGroups = computed(() =>
  pickerTarget.value.group === undefined ? [] : groupAndBelow(groups.value, pickerTarget.value.group)
);
const pickerBelow = computed(() => Math.max(pickerGroups.value.length - 1, 0));
const pickerTitle = computed(() =>
  pickerTarget.value.group !== undefined
    ? `Topic for "${groups.value[pickerTarget.value.group].title}"`
    : 'Topic for this piece'
);
const pickerSuggestedName = computed(() =>
  pickerTarget.value.group !== undefined ? groups.value[pickerTarget.value.group].title : ''
);
const newTopicName = computed(() => (pickerNewName.value.trim() || pickerSuggestedName.value).trim());

const openPicker = (target: { group?: number; chunk?: string }) => {
  pickerTarget.value = target;
  const current =
    target.group !== undefined ? groupDestination(groups.value[target.group]) : destinations.value[target.chunk!] ?? UNUSED;
  pickerValue.value = current === MIXED ? UNUSED : current;
  pickerNewName.value = '';
  pickerOpen.value = true;
};

const assign = (destination: Destination) => {
  const ids =
    pickerTarget.value.group !== undefined ? pickerGroups.value.flatMap((group) => group.chunkIds) : [pickerTarget.value.chunk!];
  const next = { ...destinations.value };
  ids.forEach((id) => (next[id] = destination));
  destinations.value = next;
  dirty.value = true;
  pickerOpen.value = false;
};

const applyPicked = () => assign(pickerValue.value ?? UNUSED);

const addNewTopic = (name: string, parent: Destination): Destination => {
  const key = `new-${++newTopicCounter}`;
  newTopics.value = [...newTopics.value, { key, name: name.trim(), parent }];
  dirty.value = true;
  return created(key);
};

const applyNewTopic = () => {
  if (newTopicName.value === '') return;
  assign(addNewTopic(newTopicName.value, pickerValue.value ?? UNUSED));
};

// New topics panel: add, rename, remove
const nameOpen = ref(false);
const renaming = ref<string | null>(null);
const name = ref('');
const nameParent = ref<Destination>(UNUSED);
const parentItems = computed<PickerItem[]>(() => [
  { title: '(top level)', value: UNUSED, depth: 0 },
  ...options.value.map((option) => ({ title: option.title, value: option.value, depth: option.depth })),
]);

const askNewTopic = () => {
  renaming.value = null;
  name.value = '';
  nameParent.value = UNUSED;
  nameOpen.value = true;
};

const askRename = (option: DestinationOption) => {
  renaming.value = option.value.slice(2);
  name.value = option.title;
  nameOpen.value = true;
};

const saveName = () => {
  const trimmed = name.value.trim();
  if (trimmed === '') return;
  if (renaming.value !== null) {
    const key = renaming.value;
    newTopics.value = newTopics.value.map((topic) => (topic.key === key ? { ...topic, name: trimmed } : topic));
    dirty.value = true;
  } else {
    addNewTopic(trimmed, nameParent.value ?? UNUSED);
  }
  nameOpen.value = false;
};

const removeTopic = (option: DestinationOption) => {
  const result = removeNewTopic(destinations.value, newTopics.value, option.value.slice(2));
  destinations.value = result.destinations;
  newTopics.value = result.newTopics;
  dirty.value = true;
};

const save = async () => {
  saving.value = true;
  try {
    await RemoteServices.distributeMaterial(
      props.materialId,
      toTree(props.materialId, chunks.value, destinations.value, newTopics.value, topics.value)
    );
    dirty.value = false;
    emit('saved');
    emit('update:dialog', false);
  } catch (error) {
    store.setError(error as string);
  }
  saving.value = false;
};

const close = () => {
  if (dirty.value && !confirm('Close without saving? The changes are lost.')) return;
  emit('update:dialog', false);
};

watch(
  () => [props.dialog, props.materialId],
  () => {
    if (props.dialog) load();
  },
  { immediate: true }
);

defineExpose({ destinations, newTopics, groups, applySuggestion });
</script>

<style lang="scss" scoped>
// Dialogs are centred like in Vuetify 2; a working view like this one reads better from the left
.distribution {
  text-align: left;
}

.document {
  max-height: calc(100vh - 210px);
  overflow-y: auto;
}

.topics-panel {
  max-height: calc(100vh - 210px);
  overflow-y: auto;
}

.group-row {
  min-height: 36px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.group-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chunk-row {
  padding: 6px 0;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.08);
}

.chunk-text {
  flex: 1;
  font-size: 0.85rem;
  white-space: pre-wrap;
  word-break: break-word;
}

.more {
  cursor: pointer;
}

.new-topic .v-text-field {
  min-width: 0;
}

.destination {
  max-width: 260px;
  flex-shrink: 0;
}
</style>
