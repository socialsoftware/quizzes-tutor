<template>
  <div>
    <v-card-title class="d-flex align-center">
      <v-spacer />
      <v-btn
        style="margin-right: 8px"
        color="primary"
        class="text-white"
        @click="getDiscussions"
        >Refresh List</v-btn
      >
      <v-btn
        v-if="!showClosedDiscussions"
        color="primary"
        class="text-white"
        @click="toggleClosedDiscussions"
        >Show Closed Discussions</v-btn
      >
      <v-btn v-else color="primary" class="text-white" @click="toggleClosedDiscussions"
        >Hide Closed Discussions</v-btn
      >
    </v-card-title>
    <discussion-list-component :discussions="discussions" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import DiscussionListComponent from '@/views/student/discussions/DiscussionListComponent.vue';

const store = useStore();
const discussions = ref<Discussion[]>([]);
const showClosedDiscussions = ref(false);

const getDiscussions = async () => {
  store.setLoading();
  try {
    if (showClosedDiscussions.value) {
      discussions.value = await RemoteServices.getCourseExecutionDiscussions();
    } else {
      discussions.value = await RemoteServices.getOpenCourseExecutionDiscussions();
    }
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};

onMounted(() => {
  getDiscussions();
});

const toggleClosedDiscussions = async () => {
  showClosedDiscussions.value = !showClosedDiscussions.value;
  await getDiscussions();
};
</script>

<style scoped></style>
