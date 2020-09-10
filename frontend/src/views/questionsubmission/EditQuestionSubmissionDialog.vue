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
        <div
          v-for="index in editQuestionSubmission.question.options.length"
          :key="index"
        >
          <v-row>
            <v-textarea
              auto-grow
              rows="2"
              v-model="
                editQuestionSubmission.question.options[index - 1].content
              "
              :label="`Option ${index}`"
              v-bind:data-cy="'Option' + index"
            ></v-textarea>
            <v-switch
              v-model="
                editQuestionSubmission.question.options[index - 1].correct
              "
              class="ma-4"
              label="Correct"
              v-bind:data-cy="'Switch' + index"
            />
          </v-row>
        </div>
        <v-textarea
          outline
          rows="1"
          v-model="comment"
          label="Comment"
          data-cy="Comment"
        ></v-textarea>
      </v-card-text>

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
          data-cy="SaveButton"
          >Save</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="submitQuestionSubmission"
          data-cy="RequestReviewButton"
          >Request Review</v-btn
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

@Component
export default class EditQuestionSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;

  editQuestionSubmission!: QuestionSubmission;
  comment: string = '';

  created() {
    this.updateQuestionSubmission();
    this.comment = '';
  }

  @Watch('questionSubmission', { immediate: true, deep: true })
  updateQuestionSubmission() {
    this.editQuestionSubmission = new QuestionSubmission(
      this.questionSubmission
    );
  }

  async saveQuestionSubmission() {
    try {
      if (
        this.comment.trim().length == 0 ||
        (this.comment.trim().length > 0 &&
          confirm('Comment will not be saved. Do you wish to proceed?'))
      ) {
        let result = await this.createQuestionSubmission();
        this.$emit('save-submission', result);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async submitQuestionSubmission() {
    try {
      if (this.comment.trim().length === 0) {
        await this.$store.dispatch(
          'error',
          'Error: Please insert a short comment that justifies your submission'
        );
        return;
      }

      let result = await this.createQuestionSubmission();
      this.$emit('submit-submission', this.comment, result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async createQuestionSubmission() {
    if (
      this.editQuestionSubmission &&
      (!this.editQuestionSubmission.question.title ||
        !this.editQuestionSubmission.question.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Error: Question must have title and content'
      );
      return;
    }

    try {
      let result;
      if (this.editQuestionSubmission.question.id != null) {
        result = await RemoteServices.updateQuestionSubmission(
          this.editQuestionSubmission
        );
      } else {
        result = await RemoteServices.createQuestionSubmission(
          this.editQuestionSubmission
        );
      }
      return result;
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
