<template>
  <v-dialog :model-value="dialog" max-width="720" @update:model-value="close">
    <v-card class="api-keys" data-cy="ApiKeysDialog">
      <v-card-title>My API keys</v-card-title>
      <v-card-text>
        <p class="text-body-2 mb-3">
          Give your own key for a provider so that your requests for questions and reply drafts are charged to your account
          and not to the service's. Only you can use it. It is checked with the provider, kept encrypted and never shown
          again: you will only see how it ends. Without a key of your own the service's key is used.
        </p>
        <v-alert v-if="keys && !keys.available" type="warning" variant="tonal" density="compact" class="mb-3" data-cy="KeysUnavailable">
          This server cannot keep API keys (it has no encryption key set). Ask the administrator.
        </v-alert>

        <div v-for="provider in keys?.keys ?? []" :key="provider.provider" class="provider-row" data-cy="ApiKeyRow">
          <div class="d-flex align-center flex-wrap ga-2">
            <span class="font-weight-medium provider-name">{{ nameOf(provider.provider) }}</span>
            <v-chip v-if="provider.configured" size="small" color="green" data-cy="KeyConfigured">
              Your key ending in {{ provider.lastFour }}
            </v-chip>
            <v-chip v-else size="small" data-cy="KeyNotSet">Not set: the service's key is used</v-chip>
            <v-spacer />
            <v-btn
              v-if="provider.configured"
              size="small"
              variant="text"
              color="red"
              :loading="working === provider.provider"
              data-cy="RemoveKey"
              @click="remove(provider.provider)"
            >
              Remove
            </v-btn>
          </div>
          <div class="d-flex align-center ga-2 mt-1">
            <v-text-field
              v-model="drafts[provider.provider]"
              type="password"
              density="compact"
              hide-details
              autocomplete="new-password"
              spellcheck="false"
              :label="provider.configured ? 'Replace with another key' : 'Paste your key'"
              :disabled="!keys?.available"
              data-cy="KeyInput"
              @keyup.enter="save(provider.provider)"
            />
            <v-btn
              color="primary"
              :disabled="!keys?.available || !valid(provider.provider)"
              :loading="working === provider.provider"
              data-cy="SaveKey"
              @click="save(provider.provider)"
            >
              Check and save
            </v-btn>
          </div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" data-cy="CloseApiKeys" @click="close(false)">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { TeacherApiKeys } from '@/models/management/generation/TeacherApiKeys';
import { PROVIDER_NAMES } from '@/models/admin/LlmSettings';

const props = defineProps<{ dialog: boolean }>();
const emit = defineEmits<{ (e: 'update:dialog', value: boolean): void }>();

const store = useStore();
const keys = ref<TeacherApiKeys | null>(null);
// What is being typed, by provider: it is dropped as soon as it is saved or the dialog closes
const drafts = reactive<Record<string, string>>({});
const working = ref<string | null>(null);

const nameOf = (provider: string) => PROVIDER_NAMES[provider] ?? provider;
const valid = (provider: string) => (drafts[provider] ?? '').trim().length >= 8;

const clearDrafts = () => Object.keys(drafts).forEach((provider) => delete drafts[provider]);

const load = async () => {
  clearDrafts();
  try {
    keys.value = await RemoteServices.getMyApiKeys();
  } catch (error) {
    store.setError(error as string);
    emit('update:dialog', false);
  }
};

const save = async (provider: string) => {
  if (!valid(provider) || working.value) return;
  working.value = provider;
  try {
    keys.value = await RemoteServices.saveMyApiKey(provider, drafts[provider].trim());
    delete drafts[provider];
  } catch (error) {
    store.setError(error as string);
  }
  working.value = null;
};

const remove = async (provider: string) => {
  if (!confirm(`Remove your ${nameOf(provider)} key? Your requests will use the service's key again.`)) return;
  working.value = provider;
  try {
    keys.value = await RemoteServices.removeMyApiKey(provider);
  } catch (error) {
    store.setError(error as string);
  }
  working.value = null;
};

const close = (open: boolean) => {
  if (!open) {
    clearDrafts();
    emit('update:dialog', false);
  }
};

watch(
  () => props.dialog,
  (open) => {
    if (open) load();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.api-keys {
  text-align: left;
}

.provider-row {
  padding: 10px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.provider-name {
  min-width: 130px;
}
</style>
