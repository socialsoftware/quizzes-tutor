<template>
  <v-container>
    <p v-if="reviews.length === 0" style="text-align: center; color: lightgrey">
      No reviews available
    </p>
    <v-container
      v-for="review in reviews"
      :key="review.id"
      v-bind:class="[
        $store.getters.getUser.username === review.username
          ? 'review-left'
          : 'review-right'
      ]"
    >
      <p>
        {{ review.creationDate + ': ' }}
        <span v-if="!review.isComment()">
          <v-chip small :color="review.getStatusColor()">
            <span>{{ review.reviewStatus() }}</span>
          </v-chip>
          {{ ' by ' }}
        </span>
        {{ review.name }}
      </p>
      <p>
        {{ "'" + review.comment + "'" }}
      </p>
    </v-container>
    <hr v-if="reviews.length !== 0" style="border: .5px lightgrey solid" />
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Review from '@/models/management/Review';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ShowReviews extends Vue {
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;

  reviews: Review[] = [];

  async created() {
    await this.getReviews();
  }

  async getReviews() {
    await this.$store.dispatch('loading');
    try {
      this.reviews = await RemoteServices.getQuestionSubmissionReviews(
        this.questionSubmission.id!
      );
      this.reviews.sort((a, b) => this.sortNewestFirst(a, b));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  sortNewestFirst(a: Review, b: Review) {
    if (a.creationDate && b.creationDate)
      return a.creationDate < b.creationDate ? 1 : -1;
    else return 0;
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
