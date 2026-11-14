<template>
  <v-dialog :model-value="dialog" max-width="760" @update:model-value="close">
    <v-card class="api-keys" data-cy="ApiKeysDialog">
      <v-card-title>My API keys</v-card-title>
      <v-card-text>
        <p class="text-body-2 mb-3">
          Give your own key for any model provider so that your requests for questions and reply drafts are charged to your
          account and not to the site's. Only you can use it. It is checked with the provider, kept encrypted and never shown
          again: you will only see how it ends. Without a key of your own the site's key is used.
        </p>
        <api-keys-panel
          ref="panel"
          :keys="keys"
          :working="working"
          empty-text="You have not given a key: your requests use the site's."
          @save="save"
          @remove="remove"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" data-cy="CloseApiKeys" @click="close(false)">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { TeacherApiKeys } from '@/models/management/generation/TeacherApiKeys';
import ApiKeysPanel from '@/views/admin/ApiKeysPanel.vue';

const props = defineProps<{ dialog: boolean }>();
const emit = defineEmits<{ (e: 'update:dialog', value: boolean): void; (e: 'changed'): void }>();

const store = useStore();
const keys = ref<TeacherApiKeys | null>(null);
const working = ref<string | null>(null);
const panel = ref<InstanceType<typeof ApiKeysPanel> | null>(null);

const load = async () => {
  panel.value?.forget();
  try {
    keys.value = await RemoteServices.getMyApiKeys();
  } catch (error) {
    store.setError(error as string);
    emit('update:dialog', false);
  }
};

const save = async ({ provider, key, model }: { provider: string; key: string; model: string }) => {
  if (working.value) return;
  working.value = provider;
  try {
    keys.value = await RemoteServices.saveMyApiKey(provider, key, model);
    panel.value?.clear();
    emit('changed');
  } catch (error) {
    store.setError(error as string);
  }
  working.value = null;
};

const remove = async (provider: string) => {
  const name = keys.value?.keys.find((key) => key.provider === provider)?.name ?? provider;
  if (!confirm(`Remove your ${name} key? Your requests will use the site's key again.`)) return;
  working.value = provider;
  try {
    keys.value = await RemoteServices.removeMyApiKey(provider);
    emit('changed');
  } catch (error) {
    store.setError(error as string);
  }
  working.value = null;
};

const close = (open: boolean) => {
  if (!open) {
    panel.value?.forget();
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
</style>
