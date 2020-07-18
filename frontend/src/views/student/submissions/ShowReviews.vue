<template>
    <v-container>
        <p v-if="reviews.length === 0" style="text-align: center; color: lightgrey"> No reviews available </p>
        <v-container v-for="review in reviews" :key="review.id" class="review">
            <p>
                {{ review.creationDate + ': ' }}
                <v-chip small :color="getStatusColor(review.status)">
                    <span>{{ reviewStatus(review.status) }}</span>
                </v-chip>
                {{ ' by ' + review.name }}
            </p>
            <p>
                {{ "'" + review.justification + "'" }}
            </p>
            <hr />
        </v-container>
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
  }
</script>

<style lang="scss" scoped>
    .review {
        border-left: solid;
        border-color: dodgerblue;
    }
</style>
