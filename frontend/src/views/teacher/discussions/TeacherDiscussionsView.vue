<template>
  <discussion-list-component :discussions="discussions" />
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import DiscussionListComponent from '@/views/student/discussions/DiscussionListComponent.vue';

@Component({
  components: {
    'discussion-list-component': DiscussionListComponent
  }
})
export default class TeacherDiscussionsView extends Vue {
  discussions: Discussion[] = [];

  async created() {
    await this.$store.dispatch('loading');
    this.discussions = await RemoteServices.getOpenCourseExecutionDiscussions();
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
