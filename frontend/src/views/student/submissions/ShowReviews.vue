<template>
  <v-container>
    <p v-if="reviews.length === 0" style="text-align: center; color: lightgrey">
      No reviews available
    </p>
    <v-container v-for="review in reviews" :key="review.id" v-bind:class="[$store.getters.getUser.name === review.name ? 'review-left' : 'review-right']">
      <p>
        {{ review.creationDate + ': ' }}
        <span v-if="review.status !== 'COMMENT'">
          <v-chip small :color="getStatusColor(review.status)">
            <span>{{ reviewStatus(review.status) }}</span>
          </v-chip>
          {{ ' by ' }}
        </span>
        {{ review.name }}
      </p>
      <p>
        {{ "'" + review.comment + "'" }}
      </p>
    </v-container>
    <hr style="border: .5px lightgrey solid"/>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Review from '@/models/management/Review';
import Submission from '@/models/management/Submission';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ShowReviews extends Vue {
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  reviews: Review[] = [];

  async created() {
    await this.getReviews();
  }

  async getReviews() {
    await this.$store.dispatch('loading');
    try {
      [this.reviews] = await Promise.all([
        RemoteServices.getSubmissionReviews(this.submission.id!)
      ]);
      this.reviews.sort((a, b) => this.sortNewestFirst(a, b));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  reviewStatus(status: string) {
    console.log(status);
    if (status === 'AVAILABLE' || status === 'DISABLED') {
      return 'APPROVED';
    } else if (status === 'REJECTED') {
      return status;
    } else if (status === 'IN_REVISION') {
      return 'CHANGES REQUESTED';
    } else if (status === 'IN_REVIEW') {
      return 'FURTHER REVIEW REQUESTED';
    }
  }

  sortNewestFirst(a: Review, b: Review) {
    if (a.creationDate && b.creationDate)
      return a.creationDate < b.creationDate ? 1 : -1;
    else return 0;
  }

  getStatusColor(status: string) {
    if (status === 'AVAILABLE' || status === 'DISABLED') return 'green';
    else if (status === 'REJECTED') return 'red';
    else if (status === 'IN_REVISION') return 'yellow';
    else if (status === 'IN_REVIEW') return 'blue';
  }

  isUser(name: string) {
    console.log('testing');
    return this.$store.getters.getUser.name === name;
  }
}
</script>

<style lang="scss" scoped>
  .review-left {
    border-style: solid;
    border-color: lightgrey lightgrey lightgrey dodgerblue;
    border-width: 1px 1px 0 10px;
  }
  .review-right {
    border-style: solid;
    border-color: lightgrey dodgerblue lightgrey lightgrey;
    border-width: 1px 10px 0 1px;
    text-align: right;
  }
</style>
