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
      <div
        class="text-left"
        v-if="
          submission.question.status === 'IN_REVISION' ||
            submission.question.status === 'IN_REVIEW'
        "
      >
        <v-card-title>
          <span class="headline">{{ 'New Comment' }}</span>
        </v-card-title>
        <v-card-text class="text-left">
          <v-textarea
            rows="1"
            v-model="comment"
            label="Comment"
            data-cy="Comment"
          ></v-textarea>
        </v-card-text>
      </div>
      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" data-cy="close" @click="$emit('dialog')"
          >close</v-btn
        >
        <v-btn
          v-if="
            submission.question.status === 'IN_REVISION' ||
              submission.question.status === 'IN_REVIEW'
          "
          color="blue darken-1"
          @click="addComment"
          data-cy="SubmitButton"
          >submit</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import ShowReviews from '@/views/student/submissions/ShowReviews.vue';
import ShowQuestion from '@/views/student/submissions/ShowQuestion.vue';
import Submission from '@/models/management/Submission';
import RemoteServices from '@/services/RemoteServices';
import Review from '@/models/management/Review';

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
  comment: string = '';
  status: string = '';

  @Watch('dialog')
  forceRerender() {
    if (this.dialog) {
      this.reviewsComponentKey += 1;
      this.comment = '';
    }
  }

  async addComment() {
    let review = new Review();
    review.submissionId = this.submission.id!;
    review.status = 'COMMENT';
    review.comment = this.comment;
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.createReview(review);
      this.$emit('dialog', false);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
<style lang="scss" scoped>
.history {
  max-height: 220px;
  overflow-y: auto;
}
</style>
