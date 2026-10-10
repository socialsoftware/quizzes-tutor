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
      :loading="modelsLoading"
      :menu-props="{ contentClass: 'text-left' }"
      data-cy="LlmModel"
      @update:model-value="changeModel"
    >
      <template v-slot:append>
        <v-btn
          icon="fas fa-sync"
          size="x-small"
          variant="text"
          :title="`Read the models of ${providerName} again`"
          :disabled="modelsLoading"
          data-cy="RefreshModels"
          @click.stop="emit('refresh-models', modelValue.provider)"
        />
      </template>
    </v-combobox>
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
  // The models the provider offers (for Ollama, the ones its server has)
  availableModels: string[];
  // Why they could not be read, if so
  modelsError: string | null;
  modelsLoading: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: LlmModelChoice): void;
  (e: 'refresh-models', provider: string): void;
}>();

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
  // Only when the list could not be read, the suggested default is offered instead
  const suggested = props.defaultModels[props.modelValue.provider];
  if (props.availableModels.length > 0 || !suggested) return props.availableModels;
  return [suggested];
});

const listed = computed(() => props.availableModels.includes(props.modelValue.model));

const modelHint = computed(() => {
  const ollama = props.modelValue.provider === 'ollama';
  if (props.modelsLoading) return `Reading the models of ${providerName.value}…`;
  if (props.modelsError) return `Could not read the models of ${providerName.value} (${props.modelsError}); type the name`;
  if (ollama) return listed.value ? 'Installed on the Ollama server' : 'Not on the Ollama server yet: download it below';
  if (props.modelValue.model && !listed.value) return `Not in ${providerName.value}'s list: it may have been withdrawn`;
  return `${props.availableModels.length} models from ${providerName.value}: pick one or type to search`;
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
