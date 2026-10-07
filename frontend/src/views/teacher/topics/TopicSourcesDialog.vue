<template>
  <v-dialog :model-value="dialog" max-width="720" @update:model-value="$emit('update:dialog', $event)">
    <v-card>
      <v-card-title>Document sections of "{{ topic.name }}"</v-card-title>
      <v-card-subtitle>
        Questions about this topic (and its subtopics) are written from these parts of your documents.
      </v-card-subtitle>
      <v-card-text>
        <p v-if="sources.length === 0" class="text-medium-emphasis" data-cy="NoSources">
          Nothing linked yet. Pick a document and its sections below.
        </p>
        <div v-for="group in groups" :key="group.materialId" class="mb-3" data-cy="SourceGroup">
          <div class="font-weight-medium">{{ group.filename }}</div>
          <v-chip
            v-for="path in group.paths"
            :key="path"
            closable
            size="small"
            class="mr-1 mt-1"
            :color="isStale(group.materialId, path) ? 'error' : undefined"
            data-cy="SourceChip"
            @click:close="removeSource(group.materialId, path)"
          >
            {{ path }}
            <span v-if="isStale(group.materialId, path)" class="ml-1">(no longer in the document)</span>
          </v-chip>
        </div>

        <v-divider class="my-4" />
        <v-row>
          <v-col cols="12" md="5">
            <v-select
              v-model="materialToAdd"
              :items="readyMaterials"
              item-title="filename"
              item-value="id"
              label="Document"
              data-cy="SourceMaterial"
            />
          </v-col>
          <v-col cols="12" md="7">
            <v-autocomplete
              v-model="sectionsToAdd"
              :items="availableSections"
              item-title="title"
              item-value="path"
              label="Sections to add"
              multiple
              chips
              closable-chips
              clearable
              :disabled="!materialToAdd"
              :loading="loadingSections"
              data-cy="SourceSections"
            >
              <template v-slot:item="{ props: itemProps, item }">
                <v-list-item v-bind="itemProps" :subtitle="(item as GenerationSection).parentPath" />
              </template>
            </v-autocomplete>
          </v-col>
        </v-row>
        <v-btn :disabled="sectionsToAdd.length === 0" data-cy="AddSources" @click="addSections">Add</v-btn>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="$emit('update:dialog', false)">Cancel</v-btn>
        <v-btn color="primary" :loading="saving" data-cy="SaveSources" @click="save">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import TopicNode, { TopicSource } from '@/models/management/TopicNode';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import GenerationSection from '@/models/management/generation/GenerationSection';

const props = defineProps<{ dialog: boolean; topic: TopicNode }>();
const emit = defineEmits<{
  (e: 'update:dialog', open: boolean): void;
  (e: 'saved', topic: TopicNode): void;
}>();

const store = useStore();

const sources = ref<TopicSource[]>(props.topic.sources.map((source) => new TopicSource(source)));
const materials = ref<GenerationMaterial[]>([]);
// The sections each document has now, loaded when needed, to spot links the document no longer has
const sectionsByMaterial = ref<Record<string, GenerationSection[]>>({});
const materialToAdd = ref<string | null>(null);
const sectionsToAdd = ref<string[]>([]);
const loadingSections = ref(false);
const saving = ref(false);

const readyMaterials = computed(() => materials.value.filter((material) => material.isReady()));

const filenameOf = (materialId: string) =>
  materials.value.find((material) => material.id === materialId)?.filename ?? '(document no longer available)';

const groups = computed(() => {
  const byMaterial = new Map<string, string[]>();
  sources.value.forEach((source) => {
    byMaterial.set(source.materialId, [...(byMaterial.get(source.materialId) ?? []), source.sectionPath]);
  });
  return [...byMaterial.entries()].map(([materialId, paths]) => ({
    materialId,
    filename: filenameOf(materialId),
    paths,
  }));
});

const loadSections = async (materialId: string) => {
  if (sectionsByMaterial.value[materialId]) return;
  loadingSections.value = true;
  try {
    sectionsByMaterial.value = {
      ...sectionsByMaterial.value,
      [materialId]: await RemoteServices.getGenerationSections(materialId),
    };
  } catch (error) {
    store.setError(error as string);
  }
  loadingSections.value = false;
};

// A link is stale when its document (or that section of it) is gone, e.g. after reprocessing it
const isStale = (materialId: string, path: string): boolean => {
  const sections = sectionsByMaterial.value[materialId];
  if (sections) return !sections.some((section) => section.path === path);
  return materials.value.length > 0 && !materials.value.some((material) => material.id === materialId);
};

const availableSections = computed(() => {
  const sections = materialToAdd.value ? sectionsByMaterial.value[materialToAdd.value] ?? [] : [];
  return sections.filter(
    (section) => !sources.value.some((source) => source.materialId === materialToAdd.value && source.sectionPath === section.path)
  );
});

const addSections = () => {
  const materialId = materialToAdd.value;
  if (!materialId) return;
  sectionsToAdd.value.forEach((path) => sources.value.push(new TopicSource({ materialId, sectionPath: path })));
  sectionsToAdd.value = [];
};

const removeSource = (materialId: string, path: string) => {
  sources.value = sources.value.filter((source) => !(source.materialId === materialId && source.sectionPath === path));
};

const save = async () => {
  saving.value = true;
  try {
    const saved = await RemoteServices.updateTopicSources(props.topic.id, sources.value);
    emit('saved', saved);
    emit('update:dialog', false);
  } catch (error) {
    store.setError(error as string);
  }
  saving.value = false;
};

watch(materialToAdd, (materialId) => {
  sectionsToAdd.value = [];
  if (materialId) loadSections(materialId);
});

onMounted(async () => {
  try {
    materials.value = await RemoteServices.getGenerationMaterials();
    // Check the links already there against the sections their documents have now
    await Promise.all(
      [...new Set(sources.value.map((source) => source.materialId))]
        .filter((id) => materials.value.some((material) => material.id === id))
        .map((id) => loadSections(id))
    );
  } catch (error) {
    store.setError(error as string);
  }
});
</script>
