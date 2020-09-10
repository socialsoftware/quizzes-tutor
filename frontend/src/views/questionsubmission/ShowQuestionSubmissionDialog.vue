<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-container fluid>
        <v-card-title>
          <span class="headline"
            >Question Submission
            <i
              v-if="
                $store.getters.isStudent &&
                  currentQuestionSubmission.isInRevision()
              "
              class="editText"
              >(Click on question to edit)</i
            ></span
          >
          <v-spacer />
          <v-chip :color="currentQuestionSubmission.getStatusColor()">
            {{ currentQuestionSubmission.status.replace('_', ' ') }}
          </v-chip>
        </v-card-title>
        <v-card
          ripple
          outlined
          class="text-left"
          id="question"
          @click="editQuestionSubmission"
        >
          <v-card-title>
            <span class="headline">{{
              currentQuestionSubmission.question.title
            }}</span>
          </v-card-title>
          <v-card-text>
            <show-question :question="currentQuestionSubmission.question" />
          </v-card-text>
        </v-card>
        <v-card-title class="headline">Reviews</v-card-title>
        <v-card outlined>
          <div
            class="text-left"
            v-if="
              $store.getters.isTeacher &&
                currentQuestionSubmission.isInDiscussion()
            "
          >
            <v-card flat>
              <v-card-text>
                <v-row align="center" class="newReview">
                  <v-textarea
                    rows="1"
                    v-model="comment"
                    label="Comment"
                    data-cy="Comment"
                  ></v-textarea>
                  <v-col cols="3">
                    <v-select
                      v-model="selected"
                      :items="statusOptions"
                      data-cy="SelectMenu"
                      chips
                    >
                      <template #selection="{ item }">
                        <v-chip small :color="item.color">{{
                          item.text
                        }}</v-chip>
                      </template>
                    </v-select>
                  </v-col>
                  <v-btn
                    color="blue darken-1"
                    @click="reviewQuestionSubmission(selected)"
                    data-cy="SubmitButton"
                    >submit</v-btn
                  >
                </v-row>
              </v-card-text>
            </v-card>
          </div>
          <div
            class="text-left"
            v-if="
              $store.getters.isStudent &&
                currentQuestionSubmission.isInDiscussion()
            "
          >
            <v-card-text>
              <v-row align="center" class="newReview">
                <v-textarea
                  rows="1"
                  v-model="comment"
                  label="Comment"
                  data-cy="Comment"
                ></v-textarea>
                <v-btn
                  color="blue darken-1"
                  @click="reviewQuestionSubmission('COMMENT')"
                  data-cy="SubmitButton"
                  >submit</v-btn
                >
              </v-row>
            </v-card-text>
          </div>
          <v-expansion-panels
            hover
            flat
            focusable
            v-bind:style="{
              'border-top': currentQuestionSubmission.isInDiscussion()
                ? '1px solid lightgrey'
                : ''
            }"
          >
            <v-expansion-panel>
              <v-expansion-panel-header>
                <span>Review Log</span>
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <show-reviews
                  class="history"
                  :key="reviewsComponentKey"
                  :questionSubmission="currentQuestionSubmission"
                  v-on:edit-question="editQuestionSubmission"
                />
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-card>
      </v-container>
      <v-card-actions>
        <v-spacer />
        <v-btn
          data-cy="CloseButton"
          color="blue darken-1"
          @click="$emit('dialog', false)"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
    <edit-question-submission-dialog
      v-if="currentQuestionSubmission"
      v-model="editQuestionSubmissionDialog"
      :questionSubmission="currentQuestionSubmission"
      v-on:save-submission="onSaveQuestionSubmission"
    />
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import QuestionSubmission from '@/models/management/QuestionSubmission';
import Review from '@/models/management/Review';
import RemoteServices from '@/services/RemoteServices';
import ShowReviews from '@/views/questionsubmission/ShowReviews.vue';
import EditQuestionSubmissionDialog from '@/views/questionsubmission/EditQuestionSubmissionDialog.vue';

@Component({
  components: {
    'show-question': ShowQuestion,
    'show-reviews': ShowReviews,
    'edit-question-submission-dialog': EditQuestionSubmissionDialog
  }
})
export default class ShowQuestionSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;

  reviewsComponentKey: number = 0;
  comment: string = '';
  statusOptions = Review.statusOptions;
  selected: Object = {};
  editQuestionSubmissionDialog: boolean = false;
  currentQuestionSubmission: QuestionSubmission = this.questionSubmission;

  created() {
    this.forceRerender();
  }

  @Watch('dialog')
  forceRerender() {
    if (this.dialog) {
      this.updateReviews();
      this.currentQuestionSubmission = this.questionSubmission;
    }
  }

  updateReviews() {
    this.reviewsComponentKey += 1;
    this.comment = '';
    this.selected = this.statusOptions[0];
  }

  async reviewQuestionSubmission(type: string) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.createReview(this.createReview(type));
      if (type == 'COMMENT') {
        this.updateReviews();
      } else {
        this.$emit('dialog', false);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  createReview(status: string) {
    let review = new Review();
    review.prepareReview(
      this.questionSubmission.id!,
      status,
      this.comment,
      this.$store.getters.getUser.id
    );
    return review;
  }

  editQuestionSubmission(e?: Event) {
    if (
      this.$store.getters.isStudent &&
      this.questionSubmission.isInRevision()
    ) {
      if (e) e.preventDefault();
      this.editQuestionSubmissionDialog = true;
    }
  }

  async onSaveQuestionSubmission(questionSubmission: QuestionSubmission) {
    this.currentQuestionSubmission = questionSubmission;
    this.editQuestionSubmissionDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.history {
  max-height: 210px;
  overflow-y: auto;
  text-align: left;
}
.newReview {
  display: flex;
  justify-content: flex-end;
  padding-right: 20px;
  padding-left: 20px;
}
.editText {
  color: gray;
  font-size: 15px;
}
</style>
