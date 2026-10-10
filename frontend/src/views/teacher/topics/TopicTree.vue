<template>
  <div class="topic-tree">
    <div class="d-flex align-center px-4 py-2">
      <span class="text-medium-emphasis">
        Questions about a topic are written from the pieces of documents under it and under its subtopics.
      </span>
      <v-spacer />
      <v-btn color="primary" data-cy="NewRootTopic" @click="askName('add', null)">New topic</v-btn>
    </div>

    <p v-if="nodes.length === 0 && !loading" class="px-4 text-medium-emphasis" data-cy="EmptyTree">
      No topics yet. Create one, or upload a document in Generate questions and put it under topics.
    </p>

    <v-treeview
      v-if="forest.length > 0"
      :items="forest"
      item-title="title"
      item-value="id"
      open-all
      density="compact"
      data-cy="TopicTree"
    >
      <template v-slot:title="{ item }">
        <span data-cy="TopicName">{{ item.title }}</span>
        <v-chip size="x-small" class="ml-2" data-cy="TopicPiecesChip" :color="subtreePieces(nodeOf(item)) ? 'primary' : undefined">
          {{ piecesLabel(nodeOf(item)) }}
        </v-chip>
        <v-chip v-if="nodeOf(item).numberOfQuestions" size="x-small" class="ml-1">
          {{ nodeOf(item).numberOfQuestions }} question(s)
        </v-chip>
      </template>
      <template v-slot:append="{ item }">
        <v-icon size="small" class="mr-2 action-button" data-cy="AddSubtopic" title="Add a subtopic" @click.stop="askName('add', nodeOf(item))">fas fa-plus</v-icon>
        <v-icon size="small" class="mr-2 action-button" data-cy="RenameTopic" title="Rename" @click.stop="askName('rename', nodeOf(item))">edit</v-icon>
        <v-icon size="small" class="mr-2 action-button" data-cy="MoveTopic" title="Move under another topic" @click.stop="askParent(nodeOf(item))">fas fa-arrows-alt</v-icon>
        <v-icon size="small" class="mr-2 action-button" data-cy="TopicPieces" title="Pieces of documents" @click.stop="editSources(nodeOf(item))">fas fa-file-alt</v-icon>
        <v-icon size="small" class="action-button" color="red" data-cy="DeleteTopic" title="Delete" @click.stop="remove(nodeOf(item))">delete</v-icon>
      </template>
    </v-treeview>

    <v-dialog v-model="nameDialog" max-width="420">
      <v-card>
        <v-card-title>{{ nameMode === 'add' ? addTitle : 'Rename topic' }}</v-card-title>
        <v-card-text>
          <v-text-field v-model="name" label="Name" autofocus data-cy="TopicNameInput" @keyup.enter="saveName" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="nameDialog = false">Cancel</v-btn>
          <v-btn color="primary" :disabled="name.trim() === ''" data-cy="SaveName" @click="saveName">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="parentDialog" max-width="420">
      <v-card v-if="target">
        <v-card-title>Move "{{ target.name }}"</v-card-title>
        <v-card-text>
          <v-select
            v-model="newParentId"
            :items="parentOptions"
            item-title="title"
            item-value="value"
            label="Under"
            data-cy="ParentSelect"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="parentDialog = false">Cancel</v-btn>
          <v-btn color="primary" data-cy="SaveParent" @click="saveParent">Move</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <topic-sources-dialog
      v-if="target && sourcesDialog"
      v-model:dialog="sourcesDialog"
      :topic="target"
      @saved="load"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { buildForest, TreeItem } from '@/services/TopicTreeEditor';
import Topic from '@/models/management/Topic';
import TopicNode from '@/models/management/TopicNode';
import TopicSourcesDialog from '@/views/teacher/topics/TopicSourcesDialog.vue';

const store = useStore();

const nodes = ref<TopicNode[]>([]);
const loading = ref(false);

const target = ref<TopicNode | null>(null);
const nameDialog = ref(false);
const nameMode = ref<'add' | 'rename'>('add');
const name = ref('');
const parentDialog = ref(false);
const newParentId = ref<number | null>(null);
const sourcesDialog = ref(false);

const forest = computed(() =>
  buildForest(nodes.value, (node) => node.id, (node) => node.parentId, (node) => node.name)
);

const nodeOf = (item: TreeItem): TopicNode => nodes.value.find((node) => node.id === item.id)!;

const subtreePieces = (node: TopicNode) => TopicNode.subtreeSources(nodes.value, node.id).length;

// Its own pieces, and the total with its subtopics when they add some
const piecesLabel = (node: TopicNode) => {
  const own = node.sources.length;
  const total = subtreePieces(node);
  if (total === 0) return 'no pieces';
  const label = `${own} piece${own === 1 ? '' : 's'}`;
  return total > own ? `${label}, ${total} with subtopics` : label;
};

const addTitle = computed(() => (target.value ? `New subtopic of "${target.value.name}"` : 'New topic'));

const load = async () => {
  loading.value = true;
  try {
    nodes.value = await RemoteServices.getTopicTree();
  } catch (error) {
    store.setError(error as string);
  }
  loading.value = false;
};

// `add` creates under `node` (or at the top when null); `rename` renames `node`
const askName = (mode: 'add' | 'rename', node: TopicNode | null) => {
  nameMode.value = mode;
  target.value = node;
  name.value = mode === 'rename' && node ? node.name : '';
  nameDialog.value = true;
};

const saveName = async () => {
  const trimmed = name.value.trim();
  if (trimmed === '') return;
  try {
    if (nameMode.value === 'add') {
      const topic = new Topic();
      topic.name = trimmed;
      topic.parentId = target.value ? target.value.id : null;
      await RemoteServices.createTopic(topic);
    } else if (target.value) {
      const topic = new Topic();
      topic.id = target.value.id;
      topic.name = trimmed;
      await RemoteServices.updateTopic(topic);
    }
    nameDialog.value = false;
    await load();
  } catch (error) {
    store.setError(error as string);
  }
};

// A topic can go under any topic except itself and the ones below it, or to the top
const parentOptions = computed(() => {
  if (!target.value) return [];
  const below = TopicNode.subtreeIds(nodes.value, target.value.id);
  return [
    { title: '(top level)', value: null },
    ...nodes.value.filter((node) => !below.has(node.id)).map((node) => ({ title: node.name, value: node.id })),
  ];
});

const askParent = (node: TopicNode) => {
  target.value = node;
  newParentId.value = node.parentId;
  parentDialog.value = true;
};

const saveParent = async () => {
  if (!target.value) return;
  try {
    await RemoteServices.moveTopic(target.value.id, newParentId.value);
    parentDialog.value = false;
    await load();
  } catch (error) {
    store.setError(error as string);
  }
};

const editSources = (node: TopicNode) => {
  target.value = node;
  sourcesDialog.value = true;
};

const remove = async (node: TopicNode) => {
  const below = nodes.value.filter((other) => other.parentId === node.id).length;
  const note = below > 0 ? ` Its ${below} subtopic(s) move up one level.` : '';
  if (!confirm(`Delete the topic "${node.name}"?${note}`)) return;
  try {
    const topic = new Topic();
    topic.id = node.id;
    await RemoteServices.deleteTopic(topic);
    await load();
  } catch (error) {
    store.setError(error as string);
  }
};

onMounted(load);
defineExpose({ load });
</script>
