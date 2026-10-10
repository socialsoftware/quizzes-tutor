<template>
  <v-card class="table llm-settings" data-cy="LlmSettings">
    <v-card-title>Question generation model</v-card-title>
    <v-card-subtitle class="text-wrap">
      Which models write the generated questions and the reply drafts in discussions. API keys are set in the
      generation service's .env file and are never shown or sent here.
    </v-card-subtitle>

    <v-card-text v-if="view && draft">
      <h3 class="text-subtitle-1 font-weight-medium mt-2">Main model</h3>
      <llm-model-row
        v-model="draft.primary"
        :providers="view.providers"
        :keys="view.keys"
        :default-models="view.defaultModels"
        :ollama-models="ollama.models"
      />

      <h3 class="text-subtitle-1 font-weight-medium mt-6">Fallbacks</h3>
      <p class="text-caption text-medium-emphasis">
        Tried in this order when the model above fails: the provider is down or overloaded, the model was withdrawn
        or it took longer than the timeout.
      </p>
      <llm-model-row
        v-for="(fallback, index) in draft.fallbacks"
        :key="index"
        v-model="draft.fallbacks[index]"
        :providers="view.providers"
        :keys="view.keys"
        :default-models="view.defaultModels"
        :ollama-models="ollama.models"
      >
        <v-btn icon="fas fa-arrow-up" size="x-small" variant="text" title="Try earlier" :disabled="index === 0" data-cy="FallbackUp" @click="moveFallback(index, -1)" />
        <v-btn
          icon="fas fa-arrow-down"
          size="x-small"
          variant="text"
          title="Try later"
          :disabled="index === draft.fallbacks.length - 1"
          data-cy="FallbackDown"
          @click="moveFallback(index, 1)"
        />
        <v-btn icon="delete" size="x-small" variant="text" color="red" title="Remove" data-cy="RemoveFallback" @click="removeFallback(index)" />
      </llm-model-row>
      <v-btn size="small" variant="text" :disabled="draft.fallbacks.length >= MAX_FALLBACKS" data-cy="AddFallback" @click="addFallback">
        Add fallback
      </v-btn>

      <h3 class="text-subtitle-1 font-weight-medium mt-6">Ollama</h3>
      <v-text-field
        v-model="draft.ollamaBaseUrl"
        label="Address of the Ollama server"
        hint="http://ollama:11434 when it runs in Docker Compose next to the service"
        persistent-hint
        :error-messages="urlError"
        data-cy="OllamaUrl"
      />
      <div class="d-flex align-center flex-wrap mt-3">
        <span class="mr-2">Installed models:</span>
        <v-chip v-for="model in ollama.models" :key="model" size="small" class="mr-1" data-cy="OllamaModel">{{ model }}</v-chip>
        <span v-if="ollama.models.length === 0 && !ollama.error" class="text-medium-emphasis">none</span>
        <v-btn icon="fas fa-sync" size="x-small" variant="text" title="Ask the Ollama server again" :loading="loadingOllama" @click="loadOllama" />
      </div>
      <v-alert v-if="ollama.error" type="warning" variant="tonal" density="compact" class="mt-2" data-cy="OllamaError">
        The Ollama server at the saved address does not answer ({{ ollama.error }}). Start it, or save the right address.
      </v-alert>
      <div class="d-flex align-center flex-wrap mt-2">
        <v-text-field
          v-model="modelToPull"
          label="Download a model"
          hint="e.g. qwen3:4b, llama3.2:3b or hf.co/openbmb/MiniCPM5-2B-GGUF:Q4_K_M (several GB)"
          persistent-hint
          class="pull-field"
          data-cy="PullModelName"
        />
        <v-btn class="ml-2" :disabled="modelToPull.trim() === ''" :loading="pulling" data-cy="PullModel" @click="pull">Download</v-btn>
      </div>
      <div v-for="(state, model) in ollama.pulls" :key="model" class="text-caption mt-1" data-cy="PullState">
        {{ model }}:
        <span :class="state === 'done' ? 'text-green' : state === 'downloading' ? 'text-primary' : 'text-error'">{{ pullLabel(state) }}</span>
      </div>

      <h3 class="text-subtitle-1 font-weight-medium mt-6">Generation</h3>
      <v-row>
        <v-col cols="12" sm="4">
          <v-text-field
            v-model.number="draft.timeout"
            type="number"
            label="Timeout per call (seconds)"
            hint="Raise it (e.g. 600) for a local model on CPU"
            persistent-hint
            :error-messages="rangeError(draft.timeout, 5, 900)"
            data-cy="LlmTimeout"
          />
        </v-col>
        <v-col cols="12" sm="4">
          <v-text-field
            v-model.number="draft.maxRetries"
            type="number"
            label="Rewrites of a draft that fails the checks"
            :error-messages="rangeError(draft.maxRetries, 0, 5)"
            data-cy="LlmRetries"
          />
        </v-col>
        <v-col cols="12" sm="4">
          <v-text-field
            v-model.number="draft.contextChars"
            type="number"
            label="Characters of material per prompt"
            hint="Smaller models need less; a topic with more text is spread over the questions"
            persistent-hint
            :error-messages="rangeError(draft.contextChars, 2000, 60000)"
            data-cy="LlmContext"
          />
        </v-col>
      </v-row>
      <v-switch
        v-model="draft.thinking"
        color="primary"
        hide-details
        label="Let NIM reasoning models think before answering (better answers, much slower)"
        data-cy="LlmThinking"
      />
      <v-switch
        v-model="draft.discussionSuggestions"
        color="primary"
        hide-details
        label="Offer teachers a reply draft in discussions (sends the student's message to the model provider)"
        data-cy="LlmDiscussions"
      />
    </v-card-text>

    <v-card-actions v-if="view && draft">
      <v-spacer />
      <v-btn variant="text" :disabled="!dirty || saving" data-cy="ResetLlmSettings" @click="reset">Undo changes</v-btn>
      <v-btn color="primary" :loading="saving" :disabled="!dirty || !valid" data-cy="SaveLlmSettings" @click="save">Save</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { createPoller } from '@/services/Polling';
import { LlmSettings, LlmSettingsView, OllamaModels } from '@/models/admin/LlmSettings';
import LlmModelRow from '@/views/admin/LlmModelRow.vue';

const MAX_FALLBACKS = 5;
const POLL_MS = 5000;

const store = useStore();

const view = ref<LlmSettingsView | null>(null);
const draft = ref<LlmSettings | null>(null);
const ollama = ref<OllamaModels>({ models: [], pulls: {}, error: null });
const loadingOllama = ref(false);
const saving = ref(false);
const pulling = ref(false);
const modelToPull = ref('');

const copy = (settings: LlmSettings): LlmSettings => JSON.parse(JSON.stringify(settings));

const dirty = computed(() => !!view.value && !!draft.value && JSON.stringify(view.value.settings) !== JSON.stringify(draft.value));

const urlError = computed(() =>
  draft.value && !/^https?:\/\/[^/\s]+/.test(draft.value.ollamaBaseUrl.trim()) ? ['An http(s) address, e.g. http://ollama:11434'] : []
);

const rangeError = (value: number, min: number, max: number) =>
  typeof value === 'number' && Number.isFinite(value) && value >= min && value <= max ? [] : [`Between ${min} and ${max}`];

const valid = computed(() => {
  const settings = draft.value;
  if (!settings || !view.value) return false;
  const choices = [settings.primary, ...settings.fallbacks];
  return (
    choices.every((choice) => choice.model.trim() !== '' && view.value!.keys[choice.provider]) &&
    urlError.value.length === 0 &&
    rangeError(settings.timeout, 5, 900).length === 0 &&
    rangeError(settings.maxRetries, 0, 5).length === 0 &&
    rangeError(settings.contextChars, 2000, 60000).length === 0 &&
    Number.isInteger(settings.maxRetries) &&
    Number.isInteger(settings.contextChars)
  );
});

const pullLabel = (state: string) =>
  state === 'done' ? 'downloaded' : state === 'downloading' ? 'downloading…' : `failed (${state})`;

const addFallback = () => {
  const provider = draft.value!.primary.provider;
  draft.value!.fallbacks.push({ provider, model: view.value!.defaultModels[provider] ?? '' });
};

const removeFallback = (index: number) => draft.value!.fallbacks.splice(index, 1);

const moveFallback = (index: number, delta: number) => {
  const fallbacks = draft.value!.fallbacks;
  const [moved] = fallbacks.splice(index, 1);
  fallbacks.splice(index + delta, 0, moved);
};

// While a model downloads, keep asking the Ollama server until it is done
const poller = createPoller(async () => {
  await loadOllama();
  return Object.values(ollama.value.pulls).includes('downloading');
}, POLL_MS);

const loadOllama = async () => {
  loadingOllama.value = true;
  try {
    ollama.value = await RemoteServices.getOllamaModels();
  } catch (error) {
    store.setError(error as string);
  }
  loadingOllama.value = false;
};

const load = async () => {
  store.setLoading();
  try {
    view.value = await RemoteServices.getLlmSettings();
    draft.value = copy(view.value.settings);
    await loadOllama();
    if (Object.values(ollama.value.pulls).includes('downloading')) poller.start();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

const reset = () => {
  if (view.value) draft.value = copy(view.value.settings);
};

const save = async () => {
  if (!draft.value) return;
  saving.value = true;
  try {
    view.value = await RemoteServices.saveLlmSettings(draft.value);
    draft.value = copy(view.value.settings);
    await loadOllama();
  } catch (error) {
    store.setError(error as string);
  }
  saving.value = false;
};

const pull = async () => {
  pulling.value = true;
  try {
    ollama.value = await RemoteServices.pullOllamaModel(modelToPull.value.trim());
    modelToPull.value = '';
    poller.start();
  } catch (error) {
    store.setError(error as string);
  }
  pulling.value = false;
};

onMounted(load);
onUnmounted(() => poller.stop());
</script>

<style lang="scss" scoped>
.llm-settings {
  text-align: left;
}

.pull-field {
  flex: 1 1 320px;
}
</style>
