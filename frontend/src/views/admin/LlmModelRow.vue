<template>
  <div class="d-flex align-center flex-wrap model-row" data-cy="LlmModelRow">
    <v-select
      :model-value="modelValue.provider"
      :items="providerItems"
      item-title="title"
      item-value="value"
      label="Provider"
      class="provider"
      :menu-props="{ contentClass: 'text-left' }"
      data-cy="LlmProvider"
      @update:model-value="changeProvider"
    />
    <v-combobox
      :model-value="modelValue.model"
      :items="modelItems"
      label="Model"
      class="model"
      :hint="modelHint"
      persistent-hint
      :menu-props="{ contentClass: 'text-left' }"
      data-cy="LlmModel"
      @update:model-value="changeModel"
    />
    <v-btn
      variant="text"
      size="small"
      :loading="testing"
      :disabled="!modelValue.model || !hasKey"
      data-cy="TestModel"
      @click="test"
    >
      Test
    </v-btn>
    <v-tooltip v-if="result" location="bottom" :disabled="result.ok">
      <template v-slot:activator="{ props: activatorProps }">
        <v-chip size="small" :color="result.ok ? 'green' : 'red'" v-bind="activatorProps" data-cy="TestResult">
          {{ result.ok ? `Answered in ${result.seconds} s` : 'Did not answer' }}
        </v-chip>
      </template>
      <span>{{ result.error }}</span>
    </v-tooltip>
    <slot />
    <div v-if="!hasKey" class="text-error text-caption w-100" data-cy="MissingKey">
      There is no API key for {{ providerName }} in the generation service's .env: set it there to use this provider.
    </div>
    <div v-if="result && !result.ok" class="text-error text-caption w-100" data-cy="TestError">{{ result.error }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { LlmModelChoice, LlmModelTest, PROVIDER_NAMES } from '@/models/admin/LlmSettings';

const props = defineProps<{
  modelValue: LlmModelChoice;
  providers: string[];
  keys: Record<string, boolean>;
  defaultModels: Record<string, string>;
  // The models the Ollama server has, offered for the Ollama provider
  ollamaModels: string[];
}>();

const emit = defineEmits<{ (e: 'update:modelValue', value: LlmModelChoice): void }>();

const store = useStore();
const testing = ref(false);
const result = ref<LlmModelTest | null>(null);

const providerName = computed(() => PROVIDER_NAMES[props.modelValue.provider] ?? props.modelValue.provider);
const hasKey = computed(() => props.keys[props.modelValue.provider] ?? false);

const providerItems = computed(() =>
  props.providers.map((provider) => ({
    value: provider,
    title: `${PROVIDER_NAMES[provider] ?? provider}${props.keys[provider] ? '' : ' (no API key)'}`,
  }))
);

const modelItems = computed(() => {
  const suggested = props.defaultModels[props.modelValue.provider];
  const models = props.modelValue.provider === 'ollama' ? [...props.ollamaModels] : [];
  if (suggested && !models.includes(suggested)) models.push(suggested);
  return models;
});

const modelHint = computed(() => {
  if (props.modelValue.provider !== 'ollama') return 'The name the provider gives the model';
  if (props.ollamaModels.includes(props.modelValue.model)) return 'Installed on the Ollama server';
  return 'Not on the Ollama server yet: download it below';
});

const changeProvider = (provider: string) => {
  result.value = null;
  emit('update:modelValue', { provider, model: props.defaultModels[provider] ?? '' });
};

const changeModel = (model: string | null) => {
  result.value = null;
  emit('update:modelValue', { provider: props.modelValue.provider, model: (model ?? '').trim() });
};

const test = async () => {
  testing.value = true;
  result.value = null;
  try {
    result.value = await RemoteServices.testLlmModel(props.modelValue);
  } catch (error) {
    store.setError(error as string);
  }
  testing.value = false;
};
</script>

<style lang="scss" scoped>
.model-row {
  gap: 0 16px;
}

.provider {
  flex: 0 0 220px;
}

.model {
  flex: 1 1 300px;
}
</style>
