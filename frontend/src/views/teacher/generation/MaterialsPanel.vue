<template>
  <v-card class="table">
    <v-card-title>
      <span>Course materials</span>
      <v-spacer />
      <v-btn color="primary" :loading="uploading" data-cy="UploadMaterial" @click="chooseFiles">
        Upload material
      </v-btn>
      <input
        ref="fileInput"
        type="file"
        hidden
        multiple
        :accept="ACCEPTED_TYPES"
        data-cy="MaterialFile"
        @change="onFilesChosen"
      />
    </v-card-title>
    <v-card-subtitle class="text-wrap">
      PDF, PowerPoint, Word or Markdown files. After a file is read, put its pieces under the topics of the course:
      questions about a topic are written from the pieces under it and under its subtopics.
    </v-card-subtitle>

    <v-data-table
      :headers="headers"
      :items="materials"
      :items-per-page="-1"
      hide-default-footer
      :mobile-breakpoint="0"
      data-cy="Materials"
    >
      <template v-slot:[`item.actions`]="{ item }">
        <v-tooltip location="bottom">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              v-if="getRaw(item).isReady()"
              class="mr-2 action-button"
              v-bind="activatorProps"
              data-cy="DistributeMaterial"
              @click="distribute(getRaw(item))"
              >fas fa-sitemap</v-icon
            >
          </template>
          <span>Put the pieces of this document under topics</span>
        </v-tooltip>
        <v-tooltip location="bottom">
          <template v-slot:activator="{ props: activatorProps }">
            <v-icon
              v-if="!getRaw(item).isProcessing()"
              class="action-button"
              v-bind="activatorProps"
              data-cy="ReprocessMaterial"
              @click="reprocess(getRaw(item))"
              >fas fa-sync</v-icon
            >
          </template>
          <span>Read the file again (its pieces leave their topics)</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.status`]="{ item }">
        <v-tooltip location="bottom" :disabled="!getRaw(item).error">
          <template v-slot:activator="{ props: activatorProps }">
            <v-chip :color="getRaw(item).getStatusColor()" size="small" v-bind="activatorProps">
              {{ getRaw(item).status }}
            </v-chip>
          </template>
          <span>{{ getRaw(item).error }}</span>
        </v-tooltip>
      </template>
      <template v-slot:[`item.parseSeconds`]="{ item }">
        {{ getRaw(item).parseSeconds ?? '-' }}
      </template>
      <template v-slot:[`item.placedChunks`]="{ item }">
        <span v-if="!getRaw(item).isReady()">-</span>
        <v-chip
          v-else
          size="small"
          :color="getRaw(item).isUndistributed() ? 'orange' : undefined"
          data-cy="PlacedChunks"
          @click="distribute(getRaw(item))"
        >
          {{ getRaw(item).placedChunks }} / {{ getRaw(item).chunkCount }}
        </v-chip>
      </template>
      <template v-slot:no-data>No material yet. Upload the slides or notes of the course.</template>
    </v-data-table>

    <distribute-material-dialog
      v-if="distributing && distributeOpen"
      v-model:dialog="distributeOpen"
      :material-id="distributing.id"
      :material-name="distributing.filename"
      @saved="onDistributed"
    />
  </v-card>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { createPoller } from '@/services/Polling';
import { withV2ColumnWidths } from '@/services/DataTableHeaders';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import DistributeMaterialDialog from '@/views/teacher/generation/DistributeMaterialDialog.vue';

const ACCEPTED_TYPES = '.pdf,.pptx,.docx,.md,.markdown,.txt';
const MAX_UPLOAD_BYTES = 100 * 1024 * 1024;
const POLL_MS = 3000;

const emit = defineEmits<{
  (e: 'update:materials', materials: GenerationMaterial[]): void;
  (e: 'topics-changed'): void;
}>();

const store = useStore();
const materials = ref<GenerationMaterial[]>([]);
const uploading = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);
const headers: any[] = withV2ColumnWidths(GenerationMaterial.headers);

const getRaw = (item: any): GenerationMaterial => (item as any).raw || item;

const distributing = ref<GenerationMaterial | null>(null);
const distributeOpen = ref(false);

const distribute = (material: GenerationMaterial) => {
  distributing.value = material;
  distributeOpen.value = true;
};

const onDistributed = async () => {
  emit('topics-changed');
  await refresh().catch((error) => store.setError(error as string));
};

// Materials seen being read in this visit: the first one to finish opens for distribution
const seenProcessing = new Set<string>();

const refresh = async (): Promise<boolean> => {
  materials.value = await RemoteServices.getGenerationMaterials();
  emit('update:materials', materials.value);

  const justRead = materials.value.find((material) => seenProcessing.has(material.id) && material.isUndistributed());
  materials.value.forEach((material) => {
    if (material.isProcessing()) seenProcessing.add(material.id);
    else seenProcessing.delete(material.id);
  });
  if (justRead && !distributeOpen.value) distribute(justRead);

  return materials.value.some((material) => material.isProcessing());
};

// Conversion runs on the server and can take minutes: keep looking until nothing is processing
const poller = createPoller(refresh, POLL_MS);

const load = async () => {
  store.setLoading();
  try {
    if (await refresh()) poller.start();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const chooseFiles = () => fileInput.value?.click();

const onFilesChosen = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const files = Array.from(input.files ?? []);
  input.value = '';
  if (files.length === 0) return;

  const tooBig = files.find((file) => file.size > MAX_UPLOAD_BYTES);
  if (tooBig) {
    store.setError(`${tooBig.name} is larger than 100 MB`);
    return;
  }

  uploading.value = true;
  try {
    for (const file of files) {
      seenProcessing.add((await RemoteServices.uploadGenerationMaterial(file)).id);
    }
    await refresh();
    poller.start();
  } catch (error) {
    store.setError(error as string);
    await refresh().catch(() => undefined);
  }
  uploading.value = false;
};

const reprocess = async (material: GenerationMaterial) => {
  const note = 'Its pieces are cut again, so they leave their topics and you put the document under topics again.';
  if (!confirm(`Read "${material.filename}" again? ${note}`)) return;
  try {
    await RemoteServices.reprocessGenerationMaterial(material.id);
    seenProcessing.add(material.id);
    emit('topics-changed');
    await refresh();
    poller.start();
  } catch (error) {
    store.setError(error as string);
  }
};

onMounted(load);
onUnmounted(() => poller.stop());
</script>
