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
            editQuestion && editQuestion.id === null
              ? 'New Code Question'
              : 'Edit Code Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-text-field v-model="editQuestion.title" label="Title" />
        <v-select
          :items="types"
          v-model="questionType"
          label="Type"
          v-on:change="typeUpdated()"
          :disabled="editQuestion.id != null"
        />

        <v-textarea
          outline
          rows="4"
          v-model="editQuestion.content"
          label="Question"
        ></v-textarea>
      </v-card-text>

      <component v-bind:is="editQuestion.type" v-model="editQuestion">
      </component>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveQuestion">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import {
  Component,
  Model,
  Prop,
  Vue,
  Watch,
  PropSync
} from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';

import CreateOrEditMultipleChoice from '@/components/questions/CreateOrEditMultipleChoice.vue';
import CreateOrEditCodeFillIn from '@/components/questions/CreateOrEditCodeFillIn.vue';
import { QuestionFactory } from '@/services/QuestionFactory';
import MultipleChoiceQuestion from '../../../models/management/questions/MultipleChoiceQuestion';

@Component({
  components: {
    multiple_choice: CreateOrEditMultipleChoice,
    code_fill_in: CreateOrEditCodeFillIn
  }
})
export default class CreateOrEditQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) question!: Question;

  types: Array<string> = ['multiple_choice', 'code_fill_in'];
  questionType: string | null = 'multiple_choice';
  editQuestion: Question = this.question;

  created() {
    // this.updateQuestion();
    this.questionType = this.editQuestion.type;
  }

  typeUpdated() {
    this.editQuestion.type = this.questionType;
    this.editQuestion = QuestionFactory.createQuestion(this.editQuestion);
  }

  async saveQuestion() {
    if (
      this.editQuestion &&
      (!this.editQuestion.title || !this.editQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      const result =
        this.editQuestion.id != null
          ? await RemoteServices.updateQuestion(this.editQuestion)
          : await RemoteServices.createQuestion(this.editQuestion);

      this.$emit('save-question', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
