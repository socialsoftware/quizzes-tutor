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
        v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
      >
        <v-card-title>
          <span class="headline">{{ 'New Review' }}</span>
        </v-card-title>
        <v-card-text class="text-left">
          <v-textarea
            rows="1"
            v-model="comment"
            label="Comment"
            data-cy="Comment"
          ></v-textarea>
          <select v-model="selected" class="select" data-cy="SelectMenu">
            <option
              v-for="option in statusOptions"
              v-bind:value="option.value"
              v-bind:key="option.value"
              class="option"
              :data-cy="`${option.value}`"
            >
              {{ option.text }}
            </option>
          </select>
        </v-card-text>
      </div>
      <v-card-actions>
        <v-spacer />
        <v-btn
          data-cy="CloseButton"
          color="blue darken-1"
          @click="$emit('dialog', false)"
          >close</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="reviewSubmission(selected)"
          data-cy="SubmitButton"
          >submit</v-btn
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
  comment: string = '';
  selected = 'COMMENT';
  statusOptions = [
    { text: 'Comment', value: 'COMMENT' },
    { text: 'Request Changes', value: 'IN_REVISION' },
    { text: 'Request Further Review', value: 'IN_REVIEW' },
    { text: 'Approve (AVAILABLE)', value: 'AVAILABLE' },
    { text: 'Approve (DISABLED)', value: 'DISABLED' },
    { text: 'Reject', value: 'REJECTED' }
  ];

  @Watch('dialog')
  forceRerender() {
    if (this.dialog) {
      this.reviewsComponentKey += 1;
      this.status = this.submission.question.status;
      this.comment = '';
      this.selected = 'COMMENT';
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
    review.comment = this.comment;
    return review;
  }
}
</script>

<style lang="scss" scoped>
.history {
  max-height: 220px;
  overflow-y: auto;
}

.select {
  border: 1px solid blue;
}

.option {
  text-align: center;
  border: 1px solid red;
}
</style>
