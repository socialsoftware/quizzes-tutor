<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="px-5">
      <v-card-title>
        <span class="headline">
          {{
            editMode(editQuestionSubmission)
              ? 'Edit Submission'
              : 'New Submission'
          }}
        </span>
      </v-card-title>

      <v-card-text class="pa-4 text-left" v-if="editQuestionSubmission">
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-text-field
              v-model="editQuestionSubmission.question.title"
              :rules="[v => !!v || 'Question title is required']"
              data-cy="QuestionTitle"
              label="Title"
              required
            />
          </v-row>

          <v-row>
            <v-textarea
              v-model="editQuestionSubmission.question.content"
              :rules="[v => !!v || 'Question content is required']"
              auto-grow
              data-cy="QuestionContent"
              label="Question"
              required
              rows="4"
            ></v-textarea>
          </v-row>

          <v-row>
            <v-col cols="1" offset="10">
              Correct
            </v-col>
          </v-row>

          <v-row
            v-for="(option, index) in editQuestionSubmission.question.questionDetailsDto.options"
            :key="index"
          >
            <v-col cols="10">
              <v-textarea
                v-model="option.content"
                :label="`Option ${index + 1}`"
                :data-cy="`Option${index + 1}`"
                rows="1"
                auto-grow
              ></v-textarea>
            </v-col>

            <v-col cols="1">
              <v-switch
                v-model="option.correct"
                inset
                :data-cy="'Switch' + index"
              />
            </v-col>

            <v-col v-if="editQuestionSubmission.question.questionDetailsDto.options.length > 2">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                    small
                    class="ma-1 action-button"
                    v-on="on"
                    @click="removeOption(index)"
                    color="red"
                    >close</v-icon
                  >
                </template>
                <span>Remove Option</span>
              </v-tooltip>
            </v-col>
          </v-row>

          <v-row>
            <v-btn class="ma-auto" color="blue darken-1" @click="addOption">Add Option</v-btn
            >
          </v-row>

          <v-row>
            <v-textarea
              outline
              rows="1"
              v-model="comment"
              label="Comment"
              data-cy="Comment"
            ></v-textarea>
          </v-row>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="red darken-1"
          @click="$emit('dialog', false)"
          data-cy="CancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="green darken-1"
          @click="createQuestionSubmission(false)"
          data-cy="SaveButton"
          >Save</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="createQuestionSubmission(true)"
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
import MultipleChoiceCreate from '@/components/multiple-choice/MultipleChoiceCreate.vue';
import Option from '@/models/management/Option';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';

@Component({
  components: {
    // TODO: USE THIS
    multiple_choice: MultipleChoiceCreate
  }
})
export default class EditQuestionSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionSubmission, required: true })
  readonly questionSubmission!: QuestionSubmission;

  editQuestionSubmission: QuestionSubmission = new QuestionSubmission(
    this.questionSubmission
  );
  comment: string = '';

  @Watch('questionSubmission', { immediate: true, deep: true })
  updateQuestionSubmission() {
    this.editQuestionSubmission = new QuestionSubmission(
      this.questionSubmission
    );
  }

  async createQuestionSubmission(requestReview: boolean) {
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
    } else if (requestReview && this.comment.trim().length == 0) {
      await this.$store.dispatch(
        'error',
        'Error: Please insert a short comment to justify your submission'
      );
      return;
    } else if (
      !requestReview &&
      this.comment.trim().length > 0 &&
      !confirm('Comment will not be saved. Do you wish to proceed?')
    ) {
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
      if (requestReview) {
        this.$emit('submit-submission', this.comment, result);
      } else {
        this.$emit('save-submission', result);
      }
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

  // TODO: THIS WILL BE USED IN MultipleChoiceCreate
  addOption() {
    (this.editQuestionSubmission.question.questionDetailsDto as MultipleChoiceQuestionDetails).options.push(new Option());
  }

  removeOption(index: number) {
    (this.editQuestionSubmission.question.questionDetailsDto as MultipleChoiceQuestionDetails).options.splice(index, 1);
  }
}
</script>
