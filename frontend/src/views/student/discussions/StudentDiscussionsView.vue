<template>
  <discussion-list-component :discussions="discussions" />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/management/Discussion';
import DiscussionListComponent from '@/views/student/discussions/DiscussionListComponent.vue';

const store = useStore();
const discussions = ref<Discussion[]>([]);

onMounted(async () => {
  store.setLoading();
  discussions.value = await RemoteServices.getUserDiscussions();
  store.clearLoading();
});
</script>

<style scoped></style>
