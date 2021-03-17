<template>
  <div>
    <v-card-title>
      <v-spacer />
      <v-btn
        style="margin-right: 2% !important"
        color="primary"
        dark
        @click="getDiscussions"
        >Refresh List</v-btn
      >
      <v-btn
        v-if="!showClosedDiscussions"
        color="primary"
        dark
        @click="toggleClosedDiscussions"
        >Show Closed Discussions</v-btn
      >
      <v-btn v-else color="primary" dark @click="toggleClosedDiscussions"
        >Hide Closed Discussions</v-btn
      >
    </v-card-title>
    <discussion-list-component :discussions="discussions" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Discussion from '@/models/management/Discussion';
import RemoteServices from '@/services/RemoteServices';
import DiscussionListComponent from '@/views/student/discussions/DiscussionListComponent.vue';

@Component({
  components: {
    'discussion-list-component': DiscussionListComponent,
  },
})
export default class TeacherDiscussionsView extends Vue {
  discussions: Discussion[] = [];
  showClosedDiscussions: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    await this.getDiscussions();
    await this.$store.dispatch('clearLoading');
  }

  async toggleClosedDiscussions() {
    this.showClosedDiscussions = !this.showClosedDiscussions;
    await this.getDiscussions();
  }

  async getDiscussions() {
    await this.$store.dispatch('loading');
    if (this.showClosedDiscussions) {
      this.discussions = await RemoteServices.getCourseExecutionDiscussions();
    } else {
      this.discussions = await RemoteServices.getOpenCourseExecutionDiscussions();
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style scoped></style>
