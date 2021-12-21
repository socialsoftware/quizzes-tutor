<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="px-5" data-cy="createOrEditQuestionDialog">
      <v-card-title>
        <span class="headline">
          {{
            editQuestion && editQuestion.id === null
              ? 'New Question'
              : 'Edit Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="pa-4 text-left" v-if="editQuestion">
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-select
              v-model="questionType"
              :rules="[(v) => !!v || 'Question type is required']"
              label="Question Type"
              required
              :items="questionTypesOptions"
              @change="updateQuestionType"
              :readonly="editQuestion.id != null"
              data-cy="questionTypeInput"
            />
          </v-row>
          <v-row>
            <v-text-field
              v-model="editQuestion.title"
              :rules="[(v) => !!v || 'Question title is required']"
              label="Title"
              required
              data-cy="questionTitleTextArea"
            />
          </v-row>

          <v-row>
            <v-textarea
              v-model="editQuestion.content"
              label="Question"
              :rules="[(v) => !!v || 'Question content is required']"
              auto-grow
              required
              data-cy="questionQuestionTextArea"
              rows="4"
            ></v-textarea>
          </v-row>

          <component
            :is="editQuestion.questionDetailsDto.type"
            :questionDetails="editQuestion.questionDetailsDto"
            :readonlyEdit="editQuestion.id != null"
          />
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="red darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn
          color="green darken-1"
          @click="saveQuestion"
          :disabled="disableCreateButton"
          data-cy="saveQuestionButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import MultipleChoiceCreate from '@/components/multiple-choice/MultipleChoiceCreate.vue';
import CodeFillInCreate from '@/components/code-fill-in/CodeFillInCreate.vue';
import CodeOrderCreate from '@/components/code-order/CodeOrderCreate.vue';
import { QuestionFactory, QuestionTypes } from '@/services/QuestionHelpers';

@Component({
  components: {
    multiple_choice: MultipleChoiceCreate,
    code_fill_in: CodeFillInCreate,
    code_order: CodeOrderCreate,
  },
})
export default class EditQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  editQuestion: Question = new Question(this.question);
  questionType: string = this.editQuestion.questionDetailsDto.type;
  disableCreateButton: Boolean = false;

  get questionTypesOptions() {
    return Object.values(QuestionTypes).map((qt) => ({
      text: qt.replace(/_/g, ' '),
      value: qt,
    }));
  }

  updateQuestionType() {
    this.editQuestion.questionDetailsDto = QuestionFactory.getFactory(
      this.questionType
    ).createEmptyQuestionDetails();
  }

  @Watch('question', { immediate: true, deep: true })
  updateQuestion() {
    this.editQuestion = new Question(this.question);
  }

  // TODO use EasyMDE with these configs
  // markdownConfigs: object = {
  //   status: false,
  //   spellChecker: false,
  //   insertTexts: {
  //     image: ['![image][image]', '']
  //   }
  // };

  async saveQuestion() {
    this.disableCreateButton = true;
    await this.$store.dispatch('loading');
    (this.$refs.form as Vue & { validate: () => boolean }).validate();
    try {
      const result =
        this.editQuestion.id != null
          ? await RemoteServices.updateQuestion(this.editQuestion)
          : await RemoteServices.createQuestion(this.editQuestion);

      this.$emit('save-question', result);
    } catch (error) {
      this.disableCreateButton = false;
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.v-select-list,
.v-select {
  text-transform: capitalize;
}
</style>
