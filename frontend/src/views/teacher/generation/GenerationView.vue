<template>
  <div class="generation">
    <v-tabs v-model="tab" color="primary" class="mx-4 mt-2">
      <v-tab value="generate" data-cy="GenerateTab">Generate</v-tab>
      <v-tab value="review" data-cy="ReviewTab">
        Review
        <v-badge v-if="newDrafts > 0" :content="newDrafts" color="primary" inline class="ml-2" />
      </v-tab>
    </v-tabs>

    <v-window v-model="tab" :touch="false">
      <v-window-item value="generate" eager>
        <materials-panel v-model:materials="materials" />
        <generation-form :materials="materials" @requested="onRequested" />
        <jobs-panel ref="jobsPanel" @imported="onImported" />
      </v-window-item>
      <v-window-item value="review" eager>
        <review-panel ref="reviewPanel" />
      </v-window-item>
    </v-window>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import GenerationJob from '@/models/management/generation/GenerationJob';
import GenerationMaterial from '@/models/management/generation/GenerationMaterial';
import MaterialsPanel from '@/views/teacher/generation/MaterialsPanel.vue';
import GenerationForm from '@/views/teacher/generation/GenerationForm.vue';
import JobsPanel from '@/views/teacher/generation/JobsPanel.vue';
import ReviewPanel from '@/views/teacher/generation/ReviewPanel.vue';

const tab = ref('generate');
const materials = ref<GenerationMaterial[]>([]);
const newDrafts = ref(0);
const jobsPanel = ref<InstanceType<typeof JobsPanel> | null>(null);
const reviewPanel = ref<InstanceType<typeof ReviewPanel> | null>(null);

const onRequested = (job: GenerationJob) => jobsPanel.value?.addJob(job);

// A finished job adds questions to the review list: refresh it and point the teacher there
const onImported = () => {
  newDrafts.value += 1;
  reviewPanel.value?.load();
};

watch(tab, (selected) => {
  if (selected === 'review') newDrafts.value = 0;
});
</script>

<style scoped>
.generation {
  padding-bottom: 24px;
}
</style>
