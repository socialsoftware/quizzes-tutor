<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editMode(editQuestionSubmission)
              ? 'Edit Submission'
              : 'New Submission'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editQuestionSubmission">
        <v-text-field
          v-model="editQuestionSubmission.question.title"
          label="Title"
          data-cy="QuestionTitle"
        />
        <v-textarea
          outline
          rows="5"
          v-model="editQuestionSubmission.question.content"
          label="Question"
          data-cy="QuestionContent"
        ></v-textarea>
        <component
            :is="editQuestionSubmission.question.questionDetailsDto.type"
            :questionDetails.sync="editQuestionSubmission.question.questionDetailsDto"
          />
      </v-card-text>
      <div v-if="editMode(questionSubmission)" class="text-left">
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
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="CancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveQuestionSubmission"
          data-cy="SubmitButton"
        >
          {{ editMode(editQuestionSubmission) ? 'Save' : 'Submit' }}</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import QuestionSubmission from '../../models/management/QuestionSubmission';
import Review from '@/models/management/Review';
import MultipleChoiceCreate from '@/components/multiple-choice/MultipleChoiceCreate.vue';

@Component({
  components: {
    multiple_choice: MultipleChoiceCreate
  }
})
export default class EditQuestionSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;

  editQuestionSubmission!: QuestionSubmission;
  comment: string = '';

  created() {
    this.updateQuestionSubmission();
  }

  @Watch('questionSubmission', { immediate: true, deep: true })
  updateQuestionSubmission() {
    this.editQuestionSubmission = new QuestionSubmission(
      this.questionSubmission
    );
  }

  async saveQuestionSubmission() {
    if (
      this.editQuestionSubmission &&
      (!this.editQuestionSubmission.question.title ||
        !this.editQuestionSubmission.question.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      let result;
      if (this.editQuestionSubmission.question.id != null) {
        result = await RemoteServices.updateQuestionSubmission(
          this.editQuestionSubmission
        );
        let review = new Review();
        review.questionSubmissionId = this.editQuestionSubmission.id!;
        review.submissionStatus = 'IN_REVISION';
        review.userId = this.$store.getters.getUser.id;
        review.comment =
          this.comment === '' ? '' : 'QUESTION EDITED: ' + this.comment;
        await RemoteServices.createReview(review);
      } else {
        result = await RemoteServices.createQuestionSubmission(
          this.editQuestionSubmission
        );
      }

      this.$emit('save-submission', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  editMode(editQuestionSubmission: QuestionSubmission) {
    return (
      editQuestionSubmission.question &&
      editQuestionSubmission.question.id !== null
    );
  }
}
</script>
