<template>
  <v-dialog :model-value="dialog" max-width="760" scrollable @update:model-value="$emit('update:dialog', $event)">
    <v-card>
      <v-card-title>Pieces of documents under "{{ topic.name }}"</v-card-title>
      <v-card-subtitle>
        Questions about this topic are written from these pieces and from the ones under its subtopics.
      </v-card-subtitle>
      <v-card-text>
        <p v-if="sources.length === 0" class="text-medium-emphasis" data-cy="NoSources">
          Nothing here yet. In Generate questions, use "Put under topics" on a document to bring pieces here.
        </p>
        <div v-else-if="loading" class="text-center py-6"><v-progress-circular indeterminate color="primary" /></div>
        <div v-for="group in groups" v-else :key="group.materialId" class="mb-4" data-cy="SourceGroup">
          <div class="font-weight-medium mb-1">{{ group.filename }}</div>
          <div v-for="source in group.sources" :key="source.chunkId" class="d-flex align-start piece" data-cy="SourcePiece">
            <div class="piece-text">
              <div v-if="chunkOf(source)" class="text-caption text-medium-emphasis">{{ chunkOf(source)!.heading }}</div>
              <span v-if="chunkOf(source)">{{ preview(chunkOf(source)!.text) }}</span>
              <span v-else class="text-error">No longer in the document (it was read again)</span>
            </div>
            <v-btn
              icon="fas fa-times"
              size="x-small"
              variant="text"
              title="Take it out of this topic"
              data-cy="RemoveSource"
              @click="removeSource(source)"
            />
          </div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="$emit('update:dialog', false)">Cancel</v-btn>
        <v-btn color="primary" :loading="saving" :disabled="!changed" data-cy="SaveSources" @click="save">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import TopicNode, { TopicSource } from '@/models/management/TopicNode';
import MaterialChunk from '@/models/management/generation/MaterialChunk';

const PREVIEW_CHARS = 200;

const props = defineProps<{
  dialog: boolean;
  topic: TopicNode;
}>();

const emit = defineEmits<{
  (e: 'update:dialog', value: boolean): void;
  (e: 'saved'): void;
}>();

const store = useStore();

const sources = ref<TopicSource[]>(props.topic.sources.map((source) => new TopicSource(source)));
const filenames = ref<Record<string, string>>({});
const chunks = ref<Record<string, MaterialChunk>>({});
const loading = ref(false);
const saving = ref(false);

const changed = computed(() => sources.value.length !== props.topic.sources.length);

const groups = computed(() => {
  const byMaterial = new Map<string, TopicSource[]>();
  sources.value.forEach((source) => {
    if (!byMaterial.has(source.materialId)) byMaterial.set(source.materialId, []);
    byMaterial.get(source.materialId)!.push(source);
  });
  return [...byMaterial.entries()].map(([materialId, list]) => ({
    materialId,
    filename: filenames.value[materialId] ?? 'A document that no longer exists',
    sources: [...list].sort((a, b) => (chunks.value[a.chunkId]?.position ?? 0) - (chunks.value[b.chunkId]?.position ?? 0)),
  }));
});

const chunkOf = (source: TopicSource) => chunks.value[source.chunkId] ?? null;

const preview = (text: string) => (text.length > PREVIEW_CHARS ? `${text.slice(0, PREVIEW_CHARS)}…` : text);

const removeSource = (source: TopicSource) => {
  sources.value = sources.value.filter((other) => other.chunkId !== source.chunkId);
};

const load = async () => {
  if (sources.value.length === 0) return;
  loading.value = true;
  try {
    const materials = await RemoteServices.getGenerationMaterials();
    filenames.value = Object.fromEntries(materials.map((material) => [material.id, material.filename]));
    const materialIds = [...new Set(sources.value.map((source) => source.materialId))].filter((id) => id in filenames.value);
    const perMaterial = await Promise.all(materialIds.map((id) => RemoteServices.getMaterialChunks(id)));
    chunks.value = Object.fromEntries(perMaterial.flat().map((chunk) => [chunk.id, chunk]));
  } catch (error) {
    store.setError(error as string);
  }
  loading.value = false;
};

const save = async () => {
  saving.value = true;
  try {
    await RemoteServices.updateTopicSources(props.topic.id, sources.value);
    emit('saved');
    emit('update:dialog', false);
  } catch (error) {
    store.setError(error as string);
  }
  saving.value = false;
};

onMounted(load);
</script>

<style lang="scss" scoped>
.piece {
  padding: 6px 0;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.08);
}

.piece-text {
  flex: 1;
  font-size: 0.85rem;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
