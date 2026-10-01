<template>
  <v-card class="table">
    <v-card-title>Generation requests</v-card-title>
    <v-data-table
      :headers="headers"
      :items="jobs"
      :items-per-page="10"
      :mobile-breakpoint="0"
      :sort-by="[{ key: 'creationDate', order: 'desc' }]"
      data-cy="Jobs"
    >
      <template v-slot:[`item.status`]="{ item }">
        <v-tooltip location="bottom">
          <template v-slot:activator="{ props: activatorProps }">
            <v-chip :color="getRaw(item).getStatusColor()" size="small" v-bind="activatorProps">
              {{ getRaw(item).getStatusName() }}
            </v-chip>
          </template>
          <span>{{ describe(getRaw(item)) }}</span>
        </v-tooltip>
      </template>
      <template v-slot:no-data>Nothing requested yet.</template>
    </v-data-table>
  </v-card>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { createPoller } from '@/services/Polling';
import { withV2ColumnWidths } from '@/services/DataTableHeaders';
import GenerationJob from '@/models/management/generation/GenerationJob';

const POLL_MS = 5000;

const emit = defineEmits<{ (e: 'imported'): void }>();

const store = useStore();
const jobs = ref<GenerationJob[]>([]);
const headers: any[] = withV2ColumnWidths(GenerationJob.headers);

const getRaw = (item: any): GenerationJob => (item as any).raw || item;

const describe = (job: GenerationJob): string => {
  if (job.status === 'FAILED') return job.error || 'The generation failed';
  if (job.status === 'IMPORTED') {
    const skipped = job.skippedCount > 0 ? `, ${job.skippedCount} skipped for lack of material` : '';
    return `${job.importedCount} question(s) waiting for review in Submissions${skipped}`;
  }
  return 'The questions are being written';
};

const replace = (job: GenerationJob) => {
  jobs.value = jobs.value.map((known) => (known.id === job.id ? job : known));
};

// Asking for a job makes the server import its questions once it is done, so a job has
// to be asked for even if nobody is looking at it
const checkPending = async (): Promise<boolean> => {
  for (const pending of jobs.value.filter((job) => job.isPending())) {
    const updated = await RemoteServices.getGenerationJob(pending.id);
    replace(updated);
    if (!updated.isPending() && updated.status === 'IMPORTED') emit('imported');
  }
  return jobs.value.some((job) => job.isPending());
};

const poller = createPoller(checkPending, POLL_MS);

const addJob = (job: GenerationJob) => {
  jobs.value = [job, ...jobs.value.filter((known) => known.id !== job.id)];
  poller.start();
};

onMounted(async () => {
  try {
    jobs.value = await RemoteServices.getGenerationJobs();
    if (jobs.value.some((job) => job.isPending())) poller.start();
  } catch (error) {
    store.setError(error as string);
  }
});

onUnmounted(() => poller.stop());

defineExpose({ addJob });
</script>
