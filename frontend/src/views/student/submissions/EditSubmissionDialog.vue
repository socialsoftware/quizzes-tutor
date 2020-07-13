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
            editSubmission.question && editSubmission.question.id === null
              ? 'New Submission'
              : 'Edit Submission'
          }}
        </span>
            </v-card-title>

            <v-card-text class="text-left" v-if="editSubmission">
                <v-text-field v-model="editSubmission.question.title" label="Title" />
                <v-textarea
                        outline
                        rows="10"
                        v-model="editSubmission.question.content"
                        label="Question"
                ></v-textarea>
                <div v-for="index in editSubmission.question.options.length" :key="index">
                    <v-switch
                            v-model="editSubmission.question.options[index - 1].correct"
                            class="ma-4"
                            label="Correct"
                    />
                    <v-textarea
                            outline
                            rows="10"
                            v-model="editSubmission.question.options[index - 1].content"
                            :label="`Option ${index}`"
                    ></v-textarea>
                </div>
            </v-card-text>

            <v-card-actions>
                <v-switch
                        v-model="editSubmission.anonymous"
                        class="ma-4"
                        label="Anonymous"
                />
                <v-spacer />
                <v-btn color="blue darken-1" @click="$emit('dialog', false)"
                >Cancel</v-btn
                >
                <v-btn color="blue darken-1" @click="saveSubmission">Save</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
  import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
  import Question from '@/models/management/Question';
  import RemoteServices from '@/services/RemoteServices';
  import Submission from '../../../models/management/Submission';

  @Component
  export default class EditSubmissionDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: Submission, required: true }) readonly submission!: Submission;

    editSubmission!: Submission;

    created() {
      this.updateSubmission();
    }

    @Watch('submission', { immediate: true, deep: true })
    updateSubmission() {
      this.editSubmission = new Submission(this.submission);
    }

    async saveSubmission() {
      if (
        this.editSubmission &&
        (!this.editSubmission.question.title || !this.editSubmission.question.content)
      ) {
        await this.$store.dispatch(
          'error',
          'Question must have title and content'
        );
        return;
      }

      try {
        const result =
          this.editSubmission.question.id != null
            ? await RemoteServices.updateQuestion(this.editSubmission.question)
            : await RemoteServices.createSubmission(this.editSubmission);

        this.$emit('save-submission', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
</script>
