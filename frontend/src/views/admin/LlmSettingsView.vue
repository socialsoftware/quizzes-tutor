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
        :available-models="modelsOf(draft.primary.provider)"
        :models-error="modelsErrorOf(draft.primary.provider)"
        :models-loading="modelsLoadingOf(draft.primary.provider)"
        @refresh-models="loadModels($event, true)"
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
        :available-models="modelsOf(fallback.provider)"
        :models-error="modelsErrorOf(fallback.provider)"
        :models-loading="modelsLoadingOf(fallback.provider)"
        @refresh-models="loadModels($event, true)"
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
      <v-row class="mt-1">
        <v-col cols="12" sm="6">
          <v-text-field
            :model-value="draft.ollamaContextLength ?? ''"
            type="number"
            label="Context window (tokens)"
            placeholder="The server's default"
            persistent-placeholder
            clearable
            :hint="contextHint"
            persistent-hint
            :error-messages="contextWindowError"
            data-cy="OllamaContext"
            @update:model-value="setContextLength"
          />
        </v-col>
        <v-col cols="12" sm="6">
          <v-select
            v-model="draft.ollamaThinking"
            :items="THINKING_OPTIONS"
            label="Thinking"
            hint="Thinking writes better questions but takes several times longer. Effort levels only change models that have them (e.g. gpt-oss)"
            persistent-hint
            :menu-props="{ contentClass: 'text-left' }"
            data-cy="OllamaThinking"
          />
        </v-col>
      </v-row>
      <v-alert v-if="contextTooSmall" type="warning" variant="tonal" density="compact" class="mt-3" data-cy="ContextTooSmall">
        A prompt carries about {{ promptTokens }} tokens with {{ draft.contextChars }} characters of material, more than the
        {{ draft.ollamaContextLength }} tokens of the window: Ollama would cut it. Raise the window or lower the characters
        of material below.
      </v-alert>
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
        label="Let NVIDIA NIM reasoning models think before answering (better answers, much slower; for Ollama see Thinking above)"
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

    <v-card-actions v-if="view && draft" class="flex-wrap">
      <div v-if="dirty && problems.length > 0" class="text-error text-caption text-left problems" data-cy="SaveProblems">
        Cannot save yet:
        <ul>
          <li v-for="problem in problems" :key="problem">{{ problem }}</li>
        </ul>
      </div>
      <v-spacer />
      <v-btn variant="text" :disabled="!dirty || saving" data-cy="ResetLlmSettings" @click="reset">Undo changes</v-btn>
      <v-btn color="primary" :loading="saving" :disabled="!dirty || !valid" data-cy="SaveLlmSettings" @click="save">Save</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { createPoller } from '@/services/Polling';
import { LlmSettings, LlmSettingsView, OllamaModels, PROVIDER_NAMES } from '@/models/admin/LlmSettings';
import LlmModelRow from '@/views/admin/LlmModelRow.vue';

const MAX_FALLBACKS = 5;
const THINKING_OPTIONS = [
  { title: "The model's default", value: 'default' },
  { title: 'Off', value: 'off' },
  { title: 'On, low effort', value: 'low' },
  { title: 'On, medium effort', value: 'medium' },
  { title: 'On, high effort', value: 'high' },
];
// Rough size of a prompt: about 3.5 characters of material per token, plus the instructions
const CHARS_PER_TOKEN = 3.5;
const INSTRUCTION_TOKENS = 1500;
const POLL_MS = 5000;

const store = useStore();

const view = ref<LlmSettingsView | null>(null);
const draft = ref<LlmSettings | null>(null);
const ollama = ref<OllamaModels>({ models: [], pulls: {}, error: null });
const loadingOllama = ref(false);
const saving = ref(false);
const pulling = ref(false);
const modelToPull = ref('');
// The models each cloud provider offers, read once per provider (Ollama's come from `ollama`)
const catalog = ref<Record<string, { models: string[]; error: string | null; loading: boolean }>>({});

const copy = (settings: LlmSettings): LlmSettings => JSON.parse(JSON.stringify(settings));

const dirty = computed(() => !!view.value && !!draft.value && JSON.stringify(view.value.settings) !== JSON.stringify(draft.value));

const urlError = computed(() =>
  draft.value && !/^https?:\/\/[^/\s]+/.test(draft.value.ollamaBaseUrl.trim()) ? ['An http(s) address, e.g. http://ollama:11434'] : []
);

const rangeError = (value: number, min: number, max: number) =>
  typeof value === 'number' && Number.isFinite(value) && value >= min && value <= max ? [] : [`Between ${min} and ${max}`];

const setContextLength = (value: string | number | null) => {
  const number = value === null || value === '' ? null : Number(value);
  draft.value!.ollamaContextLength = number;
};

const contextWindowError = computed(() => {
  const length = draft.value?.ollamaContextLength;
  return length === null || length === undefined ? [] : rangeError(length, 1024, 262144);
});

const promptTokens = computed(() =>
  draft.value ? Math.round(draft.value.contextChars / CHARS_PER_TOKEN + INSTRUCTION_TOKENS) : 0
);

const usesOllama = computed(
  () => !!draft.value && [draft.value.primary, ...draft.value.fallbacks].some((choice) => choice.provider === 'ollama')
);

const contextTooSmall = computed(
  () => usesOllama.value && !!draft.value?.ollamaContextLength && promptTokens.value > draft.value.ollamaContextLength
);

const contextHint = computed(() => `A prompt here takes about ${promptTokens.value} tokens; empty keeps the server's default`);

// What keeps the settings from being saved, so a disabled Save never looks broken
const problems = computed(() => {
  const settings = draft.value;
  if (!settings || !view.value) return [];
  const found: string[] = [];
  [settings.primary, ...settings.fallbacks].forEach((choice, index) => {
    const where = index === 0 ? 'Main model' : `Fallback ${index}`;
    if (choice.model.trim() === '') found.push(`${where}: pick or type a model`);
    if (!view.value!.keys[choice.provider])
      found.push(`${where}: there is no API key for ${PROVIDER_NAMES[choice.provider] ?? choice.provider} in the generation service's .env`);
  });
  if (urlError.value.length > 0) found.push('Ollama: the address must be an http(s) address');
  if (contextWindowError.value.length > 0) found.push('Context window: between 1024 and 262144 tokens');
  if (rangeError(settings.timeout, 5, 900).length > 0) found.push('Timeout: between 5 and 900 seconds');
  if (rangeError(settings.maxRetries, 0, 5).length > 0 || !Number.isInteger(settings.maxRetries)) found.push('Rewrites: a whole number from 0 to 5');
  if (rangeError(settings.contextChars, 2000, 60000).length > 0 || !Number.isInteger(settings.contextChars))
    found.push('Characters of material: a whole number from 2000 to 60000');
  return found;
});

const valid = computed(() => !!draft.value && !!view.value && problems.value.length === 0);

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

const modelsOf = (provider: string) => (provider === 'ollama' ? ollama.value.models : catalog.value[provider]?.models ?? []);
// Ollama's list comes with the server's state: a server that does not answer is not "model not installed"
const modelsErrorOf = (provider: string) =>
  provider === 'ollama' ? ollama.value.error ?? null : catalog.value[provider]?.error ?? null;
const modelsLoadingOf = (provider: string) =>
  // (polled while a model downloads: only the first read shows as loading)
  provider === 'ollama' ? loadingOllama.value && ollama.value.models.length === 0 : catalog.value[provider]?.loading ?? false;

const loadModels = async (provider: string, refresh = false) => {
  if (provider === 'ollama') {
    await loadOllama();
    return;
  }
  catalog.value[provider] = { models: catalog.value[provider]?.models ?? [], error: null, loading: true };
  try {
    const listed = await RemoteServices.getProviderModels(provider, refresh);
    catalog.value[provider] = { models: listed.models, error: listed.error, loading: false };
  } catch (error) {
    catalog.value[provider] = { models: [], error: error as string, loading: false };
  }
};

// Each provider in use has its list read the first time it appears
watch(
  () => (draft.value ? [draft.value.primary, ...draft.value.fallbacks].map((choice) => choice.provider) : []),
  (providers) => {
    new Set(providers).forEach((provider) => {
      if (provider !== 'ollama' && !catalog.value[provider]) loadModels(provider);
    });
  },
  { deep: true }
);

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
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
  // An Ollama server that is down takes a while to give up: the page does not wait for it
  await loadOllama();
  if (Object.values(ollama.value.pulls).includes('downloading')) poller.start();
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

.problems {
  flex: 1 1 300px;

  ul {
    padding-left: 18px;
  }
}

.pull-field {
  flex: 1 1 320px;
}
</style>
