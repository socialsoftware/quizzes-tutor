<template>
  <div class="generation">
    <materials-panel @topics-changed="topicsVersion++" />
    <generation-form :topics-version="topicsVersion" @requested="onRequested" />
    <jobs-panel ref="jobsPanel" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import GenerationJob from '@/models/management/generation/GenerationJob';
import MaterialsPanel from '@/views/teacher/generation/MaterialsPanel.vue';
import GenerationForm from '@/views/teacher/generation/GenerationForm.vue';
import JobsPanel from '@/views/teacher/generation/JobsPanel.vue';

// Generated questions are reviewed in Submissions, next to the students' ones
// Bumped when a document is put under topics, so the form shows what each topic now holds
const topicsVersion = ref(0);
const jobsPanel = ref<InstanceType<typeof JobsPanel> | null>(null);

const onRequested = (job: GenerationJob) => jobsPanel.value?.addJob(job);
</script>

<style scoped>
.generation {
  padding-bottom: 24px;
}
</style>
