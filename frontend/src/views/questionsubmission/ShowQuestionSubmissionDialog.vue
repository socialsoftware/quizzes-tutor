<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ questionSubmission.question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :question="questionSubmission.question" />
      </v-card-text>
      <v-card-title>
        <span class="headline">{{ 'Reviews' }}</span>
      </v-card-title>
      <div class="text-left">
        <show-reviews
          class="history"
          :key="reviewsComponentKey"
          :questionSubmission="questionSubmission"
        />
      </div>
      <div
        class="text-left"
        v-if="
          $store.getters.isTeacher &&
            (this.status === 'IN_REVISION' || this.status === 'IN_REVIEW')
        "
      >
        <v-card-title>
          <span class="headline">{{ 'New Review' }}</span>
        </v-card-title>
        <v-card-text class="text-left">
          <v-row align="center">
            <v-col cols="9">
              <v-textarea
                rows="1"
                v-model="comment"
                label="Comment"
                data-cy="Comment"
              ></v-textarea>
            </v-col>
            <v-col cols="3">
              <v-select
                v-model="selected"
                :items="statusOptions"
                label="Status"
                data-cy="SelectMenu"
              >
              </v-select>
            </v-col>
          </v-row>
        </v-card-text>
      </div>
      <div
        class="text-left"
        v-if="
          $store.getters.isStudent &&
            (questionSubmission.question.status === 'IN_REVISION' ||
              questionSubmission.question.status === 'IN_REVIEW')
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
        <v-btn
          data-cy="CloseButton"
          color="blue darken-1"
          @click="$emit('dialog', false)"
          >close</v-btn
        >
        <v-btn
          v-if="this.status === 'IN_REVISION' || this.status === 'IN_REVIEW'"
          color="blue darken-1"
          @click="
            reviewQuestionSubmission(
              $store.getters.isTeacher ? selected : 'COMMENT'
            )
          "
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
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Review from '@/models/management/Review';
import RemoteServices from '@/services/RemoteServices';
import ShowReviews from '@/views/questionsubmission/ShowReviews.vue';

@Component({
  components: {
    'show-question': ShowQuestion,
    'show-reviews': ShowReviews
  }
})
export default class ShowQuestionSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;

  reviewsComponentKey: number = 0;
  status: string = '';
  comment: string = '';
  selected = 'COMMENT';
  statusOptions = [
    { text: 'Comment', value: 'COMMENT' },
    { text: 'Request Changes', value: 'IN_REVISION' },
    { text: 'Request Further Review', value: 'IN_REVIEW' },
    { text: 'Available', value: 'AVAILABLE' },
    { text: 'Disabled', value: 'DISABLED' },
    { text: 'Rejected', value: 'REJECTED' }
  ];

  created() {
    this.forceRerender();
  }

  @Watch('dialog')
  forceRerender() {
    if (this.dialog) {
      this.reviewsComponentKey += 1;
      this.status = this.questionSubmission.question.status;
      this.comment = '';
      this.selected = 'COMMENT';
    }
  }

  async reviewQuestionSubmission(status: string) {
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
    review.questionSubmissionId = this.questionSubmission.id!;
    review.status = status;
    review.comment = this.comment;
    review.userId = this.$store.getters.getUser.id;
    return review;
  }
}
</script>

<style lang="scss" scoped>
.history {
  max-height: 225px;
  overflow-y: auto;
}
</style>
