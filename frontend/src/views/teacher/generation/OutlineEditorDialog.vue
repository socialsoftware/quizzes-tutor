<template>
  <v-dialog :model-value="dialog" max-width="900" scrollable @update:model-value="close">
    <v-card>
      <v-card-title>Sections of "{{ materialName }}"</v-card-title>
      <v-card-subtitle>
        Fix how the document is divided. The topics linked to a section follow it when you rename, join or cut it.
      </v-card-subtitle>
      <v-card-text>
        <p v-if="!loading && outline.length === 0" class="text-medium-emphasis" data-cy="NoOutline">
          This document has no sections.
        </p>
        <v-list density="compact" data-cy="Outline">
          <v-list-item
            v-for="(node, index) in outline"
            :key="node.path"
            :style="{ paddingLeft: `${(node.depth - 1) * 24 + 16}px` }"
            data-cy="OutlineNode"
          >
            <v-list-item-title>
              {{ node.title }}
              <v-chip v-if="!node.hasText" size="x-small" class="ml-2">heading only</v-chip>
            </v-list-item-title>
            <v-list-item-subtitle v-if="node.preview">{{ node.preview }}</v-list-item-subtitle>
            <template v-slot:append>
              <v-icon size="small" class="mr-2 action-button" title="Rename" data-cy="RenameSection" @click="askRename(node)">edit</v-icon>
              <v-icon
                size="small"
                class="mr-2 action-button"
                :disabled="index === 0"
                title="Join with the section before"
                data-cy="MergeSection"
                @click="merge(node)"
              >fas fa-compress-alt</v-icon>
              <v-icon
                size="small"
                class="mr-2 action-button"
                :disabled="!canPromote(node)"
                title="Move up a level"
                data-cy="PromoteSection"
                @click="shift(node, -1)"
              >fas fa-outdent</v-icon>
              <v-icon
                size="small"
                class="mr-2 action-button"
                :disabled="!canDemote(index)"
                title="Move under the section above"
                data-cy="DemoteSection"
                @click="shift(node, 1)"
              >fas fa-indent</v-icon>
              <v-icon
                size="small"
                class="action-button"
                :disabled="node.paragraphCount < 2"
                title="Cut this section in two"
                data-cy="SplitSection"
                @click="askSplit(node)"
              >fas fa-cut</v-icon>
            </template>
          </v-list-item>
        </v-list>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" data-cy="CloseOutline" @click="close(false)">Done</v-btn>
      </v-card-actions>
    </v-card>

    <v-dialog v-model="renameOpen" max-width="420">
      <v-card>
        <v-card-title>Rename section</v-card-title>
        <v-card-text>
          <v-text-field v-model="name" label="Name" autofocus data-cy="SectionNameInput" @keyup.enter="saveRename" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="renameOpen = false">Cancel</v-btn>
          <v-btn color="primary" :disabled="name.trim() === ''" data-cy="SaveSectionName" @click="saveRename">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="splitOpen" max-width="720" scrollable>
      <v-card v-if="target">
        <v-card-title>Cut "{{ target.title }}"</v-card-title>
        <v-card-subtitle>Click the paragraph where the new section starts.</v-card-subtitle>
        <v-card-text>
          <v-text-field v-model="name" label="Name of the new section" data-cy="NewSectionName" />
          <div
            v-for="paragraph in paragraphs"
            :key="paragraph.index"
            class="paragraph pa-2 mb-1"
            :class="{ 'paragraph-start': paragraph.index === startAt, 'paragraph-new': startAt !== null && paragraph.index > startAt }"
            data-cy="Paragraph"
            @click="startAt = paragraph.index === 0 ? startAt : paragraph.index"
          >
            <v-chip v-if="paragraph.index === startAt" size="x-small" color="primary" class="mr-2">new section starts here</v-chip>
            {{ paragraph.text }}
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="splitOpen = false">Cancel</v-btn>
          <v-btn
            color="primary"
            :disabled="startAt === null || name.trim() === ''"
            data-cy="SaveSplit"
            @click="saveSplit"
          >Cut</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import OutlineNode, { OutlineEdit, Paragraph } from '@/models/management/generation/OutlineNode';

// Markdown has no headings below the sixth level
const MAX_DEPTH = 6;

const props = defineProps<{ dialog: boolean; materialId: string; materialName: string }>();
const emit = defineEmits<{
  (e: 'update:dialog', open: boolean): void;
  // The sections changed: what depends on them (topic links, the generation form) has to be read again
  (e: 'edited'): void;
}>();

const store = useStore();

const outline = ref<OutlineNode[]>([]);
const loading = ref(false);
const target = ref<OutlineNode | null>(null);
const renameOpen = ref(false);
const splitOpen = ref(false);
const name = ref('');
const paragraphs = ref<Paragraph[]>([]);
const startAt = ref<number | null>(null);

const canPromote = (node: OutlineNode) => node.depth > 1;

// It goes under the section just above it, so there must be one at the same level before it
const canDemote = (index: number): boolean => {
  const node = outline.value[index];
  if (node.depth >= MAX_DEPTH) return false;
  for (let before = index - 1; before >= 0; before--) {
    if (outline.value[before].depth < node.depth) return false;
    if (outline.value[before].depth === node.depth) return true;
  }
  return false;
};

const load = async () => {
  loading.value = true;
  try {
    outline.value = await RemoteServices.getGenerationOutline(props.materialId);
  } catch (error) {
    store.setError(error as string);
  }
  loading.value = false;
};

// The service says whether it can be done; a refusal is shown and nothing changes
const run = async (edit: OutlineEdit): Promise<boolean> => {
  try {
    const result = await RemoteServices.editGenerationOutline(props.materialId, edit);
    outline.value = result.outline;
    emit('edited');
    return true;
  } catch (error) {
    store.setError(error as string);
    return false;
  }
};

const askRename = (node: OutlineNode) => {
  target.value = node;
  name.value = node.title;
  renameOpen.value = true;
};

const saveRename = async () => {
  if (!target.value || name.value.trim() === '') return;
  if (await run({ op: 'rename', path: target.value.path, title: name.value.trim() })) renameOpen.value = false;
};

const merge = (node: OutlineNode) => {
  const note = node.hasText ? ' Its text becomes part of the section before it.' : '';
  if (!confirm(`Remove the heading "${node.title}"?${note} Its subsections move up a level.`)) return;
  return run({ op: 'merge', path: node.path });
};

const shift = (node: OutlineNode, delta: -1 | 1) => run({ op: 'shift', path: node.path, delta });

const askSplit = async (node: OutlineNode) => {
  try {
    paragraphs.value = await RemoteServices.getGenerationSectionText(props.materialId, node.path);
  } catch (error) {
    store.setError(error as string);
    return;
  }
  target.value = node;
  name.value = '';
  startAt.value = null;
  splitOpen.value = true;
};

const saveSplit = async () => {
  if (!target.value || startAt.value === null || name.value.trim() === '') return;
  const edit: OutlineEdit = {
    op: 'split',
    path: target.value.path,
    title: name.value.trim(),
    paragraph: startAt.value,
  };
  if (await run(edit)) splitOpen.value = false;
};

const close = (open: boolean) => emit('update:dialog', open);

onMounted(load);
defineExpose({ outline, askRename, saveRename, merge, shift, askSplit, saveSplit, canDemote, startAt, name, paragraphs });
</script>

<style scoped>
.paragraph {
  cursor: pointer;
  border-left: 3px solid transparent;
  white-space: pre-wrap;
}
.paragraph:hover {
  background: rgba(var(--v-theme-primary), 0.08);
}
.paragraph-start {
  border-left-color: rgb(var(--v-theme-primary));
}
.paragraph-new {
  background: rgba(var(--v-theme-primary), 0.05);
}
</style>
