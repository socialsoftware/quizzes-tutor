<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ submission.question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :question="submission.question" />
        <div v-if="submission.argument !== null">
          <h3 style="display: inline">Argument: </h3>
          <p style="display: inline">{{ submission.argument }}</p>
        </div>
      </v-card-text>
      <v-card-title>
        <span class="headline">{{ 'Reviews' }}</span>
      </v-card-title>
      <div class="text-left">
        <show-reviews
          class="history"
          :key="reviewsComponentKey"
          :submission="submission"
        />
      </div>
      <div class="text-left"
           v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
      >
        <v-card-title>
          <span class="headline">{{ 'New Review' }}</span>
        </v-card-title>
        <v-card-text class="text-left">
          <v-textarea
            rows="1"
            v-model="justification"
            label="Justification"
            data-cy="Justification"
          ></v-textarea>
        </v-card-text>
      </div>
      <v-card-actions>
        <v-spacer />
        <v-btn data-cy="CloseButton"
          color="blue darken-1"
          @click="$emit('dialog', false)"
          >close</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="reviewSubmission('IN_REVIEW')"
          data-cy="InReviewButton"
          >request further review</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="reviewSubmission('IN_REVISION')"
          data-cy="InRevisionButton"
        >request changes</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="reviewSubmission('REJECTED')"
          data-cy="RejectedButton"
        >rejected</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="reviewSubmission('DISABLED')"
          data-cy="DisabledButton"
          >disabled</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="reviewSubmission('AVAILABLE')"
          data-cy="AvailableButton"
          >available</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import ShowReviews from '@/views/teacher/submissions/ShowReviews.vue';
import Submission from '../../../models/management/Submission';
import Review from '@/models/management/Review';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'show-question': ShowQuestion,
    'show-reviews': ShowReviews
  }
})
export default class ShowSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  reviewsComponentKey: number = 0;
  status: string = '';
  justification: string = '';

  @Watch('dialog')
  forceRerender() {
    if (this.dialog) {
      this.reviewsComponentKey += 1;
      this.status = this.submission.question.status;
      this.justification = '';
    }
  }

  async reviewSubmission(status: string) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.createReview(this.createReview(status));
      this.$emit('dialog', false);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  createReview(status: string) {
    let review = new Review();
    review.submissionId = this.submission.id!;
    review.status = status;
    review.justification = this.justification;
    return review;
  }
}
</script>

<style lang="scss" scoped>
.history {
  max-height: 235px;
  overflow-y: auto;
}
</style>
