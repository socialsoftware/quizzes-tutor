<template>
  <v-dialog :model-value="dialog" max-width="880" scrollable @update:model-value="$emit('update:dialog', $event)">
    <v-card>
      <v-card-title>Topics from "{{ materialName }}"</v-card-title>
      <v-card-subtitle>
        A proposal built from the headings of the document. Rename, move or remove topics as you like; nothing is created until you save.
      </v-card-subtitle>
      <v-card-text style="max-height: 60vh">
        <v-progress-linear v-if="loading" indeterminate />
        <p v-else-if="nodes.length === 0" class="text-medium-emphasis" data-cy="NoProposal">
          This document has no sections to propose topics from.
        </p>

        <v-treeview
          v-if="forest.length > 0"
          :items="forest"
          item-title="title"
          item-value="id"
          open-all
          density="compact"
          data-cy="SuggestionTree"
        >
          <template v-slot:title="{ item }">
            <span data-cy="SuggestedName" :class="{ 'text-error': problems.has(nodeOf(item).key) }">{{ nodeOf(item).name }}</span>
            <v-chip v-if="nodeOf(item).sources.length" size="x-small" class="ml-2" color="primary">
              {{ nodeOf(item).sources.length }} section{{ nodeOf(item).sources.length === 1 ? '' : 's' }}
            </v-chip>
            <v-chip v-if="nodeOf(item).existingTopicId !== null" size="x-small" class="ml-1" data-cy="JoinsExisting">
              already a topic: sections are added to it
            </v-chip>
            <span v-if="problems.has(nodeOf(item).key)" class="ml-2 text-error text-caption" data-cy="NodeProblem">
              {{ problems.get(nodeOf(item).key) }}
            </span>
          </template>
          <template v-slot:append="{ item }">
            <v-icon size="small" class="mr-2 action-button" data-cy="RenameNode" title="Rename" @click.stop="askName(nodeOf(item))">edit</v-icon>
            <v-icon size="small" class="mr-2 action-button" data-cy="MoveNode" title="Move under another topic" @click.stop="askParent(nodeOf(item))">fas fa-arrows-alt</v-icon>
            <v-icon
              v-if="nodeOf(item).existingTopicId !== null"
              size="small"
              class="mr-2 action-button"
              data-cy="UnlinkNode"
              title="Create it as a new topic instead"
              @click.stop="unlink(nodeOf(item))"
              >fas fa-unlink</v-icon
            >
            <v-icon size="small" class="action-button" color="red" data-cy="RemoveNode" title="Remove (its sections go to the topic above)" @click.stop="remove(nodeOf(item))">delete</v-icon>
          </template>
        </v-treeview>
      </v-card-text>
      <v-card-actions>
        <span class="text-medium-emphasis ml-2" data-cy="ProposalSummary">{{ summary }}</span>
        <v-spacer />
        <v-btn variant="text" @click="$emit('update:dialog', false)">Cancel</v-btn>
        <v-btn color="primary" :disabled="!canSave" :loading="saving" data-cy="SaveTopics" @click="save">Create topics</v-btn>
      </v-card-actions>
    </v-card>

    <v-dialog v-model="nameDialog" max-width="420">
      <v-card>
        <v-card-title>Rename topic</v-card-title>
        <v-card-text><v-text-field v-model="name" label="Name" autofocus data-cy="NodeNameInput" @keyup.enter="saveName" /></v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="nameDialog = false">Cancel</v-btn>
          <v-btn color="primary" :disabled="name.trim() === ''" data-cy="SaveNodeName" @click="saveName">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="parentDialog" max-width="420">
      <v-card v-if="target">
        <v-card-title>Move "{{ target.name }}"</v-card-title>
        <v-card-text>
          <v-select v-model="newParentKey" :items="parentOptions" item-title="title" item-value="value" label="Under" data-cy="NodeParentSelect" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="parentDialog = false">Cancel</v-btn>
          <v-btn color="primary" data-cy="SaveNodeParent" @click="saveParent">Move</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import TopicTreeNode from '@/models/management/TopicTreeNode';
import {
  TreeItem,
  buildForest,
  linkToExisting,
  moveNode,
  nameProblems,
  removeNode,
  renameNode,
  subtreeKeys,
} from '@/services/TopicTreeEditor';

const props = defineProps<{ dialog: boolean; materialId: string; materialName: string }>();
const emit = defineEmits<{
  (e: 'update:dialog', open: boolean): void;
  (e: 'saved'): void;
}>();

const store = useStore();

const nodes = ref<TopicTreeNode[]>([]);
const existingNames = ref<string[]>([]);
const loading = ref(false);
const saving = ref(false);

const target = ref<TopicTreeNode | null>(null);
const nameDialog = ref(false);
const name = ref('');
const parentDialog = ref(false);
const newParentKey = ref<string | null>(null);

const forest = computed(() =>
  buildForest(nodes.value, (node) => node.key, (node) => node.parentKey, (node) => node.name)
);
const nodeOf = (item: TreeItem): TopicTreeNode => nodes.value.find((node) => node.key === item.id)!;

const problems = computed(() => nameProblems(nodes.value, existingNames.value));
const canSave = computed(() => nodes.value.length > 0 && problems.value.size === 0 && !loading.value);

const summary = computed(() => {
  const created = nodes.value.filter((node) => node.existingTopicId === null).length;
  const sections = nodes.value.reduce((total, node) => total + node.sources.length, 0);
  return nodes.value.length === 0 ? '' : `${created} new topic(s), ${sections} section(s) linked`;
});

const askName = (node: TopicTreeNode) => {
  target.value = node;
  name.value = node.name;
  nameDialog.value = true;
};

const saveName = () => {
  if (target.value && name.value.trim() !== '') {
    nodes.value = renameNode(nodes.value, target.value.key, name.value.trim());
    nameDialog.value = false;
  }
};

// A topic can go under any topic except itself and the ones below it, or to the top
const parentOptions = computed(() => {
  if (!target.value) return [];
  const below = subtreeKeys(nodes.value, target.value.key);
  return [
    { title: '(top level)', value: null },
    ...nodes.value.filter((node) => !below.has(node.key)).map((node) => ({ title: node.name, value: node.key })),
  ];
});

const askParent = (node: TopicTreeNode) => {
  target.value = node;
  newParentKey.value = node.parentKey;
  parentDialog.value = true;
};

const saveParent = () => {
  if (!target.value) return;
  const moved = moveNode(nodes.value, target.value.key, newParentKey.value);
  if (moved) nodes.value = moved;
  parentDialog.value = false;
};

const remove = (node: TopicTreeNode) => {
  nodes.value = removeNode(nodes.value, node.key);
};

const unlink = (node: TopicTreeNode) => {
  nodes.value = linkToExisting(nodes.value, node.key, null);
};

const save = async () => {
  saving.value = true;
  try {
    await RemoteServices.saveTopicTree(nodes.value);
    emit('saved');
    emit('update:dialog', false);
  } catch (error) {
    store.setError(error as string);
  }
  saving.value = false;
};

onMounted(async () => {
  loading.value = true;
  try {
    const [proposal, topics] = await Promise.all([
      RemoteServices.getTopicSuggestion(props.materialId),
      RemoteServices.getTopicTree(),
    ]);
    nodes.value = proposal;
    existingNames.value = topics.map((topic) => topic.name);
  } catch (error) {
    store.setError(error as string);
  }
  loading.value = false;
});
</script>
