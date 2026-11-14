<template>
  <div class="api-keys-panel" data-cy="ApiKeysPanel">
    <v-alert v-if="keys && !keys.available" type="warning" variant="tonal" density="compact" class="mb-3" data-cy="KeysUnavailable">
      This server cannot keep API keys (it has no encryption key set). Ask whoever runs the server.
    </v-alert>

    <div v-if="keys && keys.keys.length === 0" class="text-medium-emphasis mb-2" data-cy="NoKeys">{{ emptyText }}</div>
    <div v-for="key in keys?.keys ?? []" :key="key.provider" class="key-row d-flex align-center flex-wrap ga-2" data-cy="ApiKeyRow">
      <span class="font-weight-medium provider-name" data-cy="KeyProvider">{{ key.name }}</span>
      <v-chip size="small" color="green" data-cy="KeyConfigured">Key ending in {{ key.lastFour }}</v-chip>
      <v-spacer />
      <v-btn
        size="small"
        variant="text"
        color="red"
        :loading="working === key.provider"
        data-cy="RemoveKey"
        @click="$emit('remove', key.provider)"
      >
        Remove
      </v-btn>
    </div>

    <div class="add-key mt-3" data-cy="AddKey">
      <div class="d-flex align-center flex-wrap ga-2">
        <v-autocomplete
          v-model="provider"
          :items="providerItems"
          item-title="title"
          item-value="value"
          label="Provider"
          placeholder="Choose a provider"
          persistent-placeholder
          variant="outlined"
          density="compact"
          hide-details
          class="provider-field"
          :disabled="!keys?.available"
          :menu-props="{ contentClass: 'text-left' }"
          data-cy="KeyProviderSelect"
        />
        <v-text-field
          v-model="draft"
          type="password"
          variant="outlined"
          density="compact"
          hide-details
          autocomplete="new-password"
          spellcheck="false"
          :label="alreadyGiven ? 'Replace with another key' : 'API key'"
          :placeholder="provider ? 'Paste the key here' : 'Choose a provider first'"
          persistent-placeholder
          :disabled="!keys?.available || !provider"
          class="key-field"
          data-cy="KeyInput"
          @keyup.enter="save"
        />
        <v-btn color="primary" :disabled="!canSave" :loading="isWorking" data-cy="SaveKey" @click="save">
          Check and save
        </v-btn>
      </div>
      <v-text-field
        v-if="provider"
        v-model="model"
        variant="outlined"
        density="compact"
        hide-details
        autocomplete="off"
        spellcheck="false"
        label="Model to check the key with (optional)"
        :placeholder="`The usual one of ${nameOf(provider)}`"
        persistent-placeholder
        class="mt-2"
        data-cy="KeyCheckModel"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { TeacherApiKeys } from '@/models/management/generation/TeacherApiKeys';

const props = defineProps<{
  keys: TeacherApiKeys | null;
  // The provider whose key is being checked or removed
  working: string | null;
  emptyText: string;
}>();

const emit = defineEmits<{
  (e: 'save', value: { provider: string; key: string; model: string }): void;
  (e: 'remove', provider: string): void;
}>();

const provider = ref<string | null>(null);
// What is being typed: dropped as soon as it is saved
const draft = ref('');
const model = ref('');

const providerItems = computed(() => (props.keys?.providers ?? []).map((item) => ({ title: item.name, value: item.id })));
const nameOf = (id: string) => props.keys?.providers.find((item) => item.id === id)?.name ?? id;
const alreadyGiven = computed(() => !!provider.value && !!props.keys?.keys.some((key) => key.provider === provider.value));
// Only while a key is really being checked or removed (nothing in progress is `null`, as is no provider picked)
const isWorking = computed(() => props.working !== null && props.working === provider.value);
const canSave = computed(() => !!props.keys?.available && !!provider.value && draft.value.trim().length >= 8 && !props.working);

const save = () => {
  if (!canSave.value) return;
  emit('save', { provider: provider.value!, key: draft.value.trim(), model: model.value.trim() });
};

/** The parent calls this once the key was saved, so the key is not kept on the page. */
const clear = () => {
  draft.value = '';
  model.value = '';
  provider.value = null;
};

/** The parent calls this when the panel is closed or reopened. */
const forget = () => {
  draft.value = '';
  model.value = '';
};

defineExpose({ clear, forget });
</script>

<style lang="scss" scoped>
.api-keys-panel {
  text-align: left;
}

.key-row {
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.provider-name {
  min-width: 140px;
}

.provider-field {
  flex: 1 1 220px;
}

.key-field {
  flex: 2 1 260px;
}
</style>
