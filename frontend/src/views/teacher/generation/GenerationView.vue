<template>
  <div class="generation">
    <v-card class="table mb-4 pa-3 d-flex align-center flex-wrap ga-3" data-cy="ApiKeyCard">
      <v-icon>fas fa-key</v-icon>
      <div class="text-left">
        <div class="font-weight-medium">API key for question generation</div>
        <div class="text-caption text-medium-emphasis" data-cy="ApiKeyStatus">{{ keyStatus }}</div>
      </div>
      <v-spacer />
      <v-btn color="primary" variant="elevated" data-cy="MyApiKeys" @click="keysOpen = true">My API keys</v-btn>
    </v-card>
    <api-keys-dialog v-model:dialog="keysOpen" @changed="onKeysChanged" />
    <materials-panel @topics-changed="topicsVersion++" />
    <generation-form :topics-version="topicsVersion" :keys-version="keysVersion" @requested="onRequested" />
    <jobs-panel ref="jobsPanel" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import RemoteServices from '@/services/RemoteServices';
import { PROVIDER_NAMES } from '@/models/admin/LlmSettings';
import { TeacherApiKeys } from '@/models/management/generation/TeacherApiKeys';
import GenerationJob from '@/models/management/generation/GenerationJob';
import ApiKeysDialog from '@/views/teacher/generation/ApiKeysDialog.vue';
import MaterialsPanel from '@/views/teacher/generation/MaterialsPanel.vue';
import GenerationForm from '@/views/teacher/generation/GenerationForm.vue';
import JobsPanel from '@/views/teacher/generation/JobsPanel.vue';

// Generated questions are reviewed in Submissions, next to the students' ones
// Bumped when a document is put under topics, so the form shows what each topic now holds
const topicsVersion = ref(0);
const keysOpen = ref(false);
// Bumped when the teacher gives or removes an API key, so the form offers the models that key allows
const keysVersion = ref(0);
const myKeys = ref<TeacherApiKeys | null>(null);

const keyStatus = computed(() => {
  const own = (myKeys.value?.keys ?? []).filter((key) => key.configured).map((key) => PROVIDER_NAMES[key.provider] ?? key.provider);
  return own.length > 0
    ? `Your requests use your own key: ${own.join(', ')}`
    : "Your requests use the site's key. You can give your own to pick other models";
});

const loadKeys = async () => {
  try {
    myKeys.value = await RemoteServices.getMyApiKeys();
  } catch {
    // the card still works without the status
  }
};

const onKeysChanged = () => {
  keysVersion.value++;
  loadKeys();
};

onMounted(loadKeys);
const jobsPanel = ref<InstanceType<typeof JobsPanel> | null>(null);

const onRequested = (job: GenerationJob) => jobsPanel.value?.addJob(job);
</script>

<style scoped>
.generation {
  padding-bottom: 24px;
}
</style>
