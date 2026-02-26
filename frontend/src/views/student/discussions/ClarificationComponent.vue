<template>
  <div class="discussion" v-if="clarifications.length > 0">
    <ul>
      <li
        style="margin-bottom: 25px !important"
        v-for="clarification in clarifications"
        :key="clarification.id"
      >
        <div class="text-left">
          <b
            >{{ clarification.name }} ({{ clarification.username }}) replied on
            {{ clarification.date }} :</b
          >
          <span v-html="convertMarkDownText(clarification.message)" />
          <v-switch
            v-if="canChange"
            style="width: 12%"
            v-model="clarification.public"
            :label="clarification.public ? 'Public' : 'Private'"
            @change="changeReplyAvailability(clarification.id)"
          />
        </div>
      </li>
    </ul>
  </div>
  <div v-else>
    <v-card-text class="text-center"> No clarifications yet </v-card-text>
  </div>
</template>

<script setup lang="ts">
import { useStore } from '@/store';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Reply from '@/models/management/Reply';
import RemoteServices from '@/services/RemoteServices';

const props = defineProps<{
  clarifications: Reply[];
  canChange: boolean;
}>();

const emit = defineEmits(['make-private']);
const store = useStore();

const convertMarkDownText = (text: string) => {
  return convertMarkDown(text, null);
};

const changeReplyAvailability = async (id: number) => {
  store.setLoading();
  await RemoteServices.changeReplyAvailability(id);
  store.clearLoading();
  emit('make-private', id);
};
</script>

<style lang="scss" scoped>
ul {
  list-style-type: none;
}
</style>
