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
            editQuestion && editQuestion.id === null
              ? 'New Question'
              : 'Edit Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="pa-4 text-left" v-if="editQuestion">
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-text-field
              v-model="editQuestion.title"
              :rules="[v => !!v || 'Question title is required']"
              label="Title"
              required
            />
          </v-row>

          <v-row>
            <v-textarea
              v-model="editQuestion.content"
              label="Question"
              :rules="[v => !!v || 'Question content is required']"
              auto-grow
              required
              rows="4"
            ></v-textarea>
          </v-row>

          <v-row>
            <v-col cols="1" offset="10">
              Correct
            </v-col>
          </v-row>

          <v-row v-for="(option, index) in editQuestion.questionDetailsDto.options" :key="index">
            <v-col cols="10">
              <v-textarea
                v-model="option.content"
                :label="`Option ${index + 1}`"
                rows="1"
                auto-grow
              ></v-textarea>
            </v-col>
            <v-col cols="1">
              <v-switch v-model="option.correct" inset />
            </v-col>
            <v-col v-if="editQuestion.questionDetailsDto.options.length > 2">
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
            <v-btn class="ma-auto" color="blue darken-1" @click="addOption"
              >Add Option</v-btn
            >
          </v-row>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="red darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="green darken-1" @click="saveQuestion">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import Option from '@/models/management/Option';
import MultipleChoiceQuestionDetails from '@/models/management/questions/MultipleChoiceQuestionDetails';

// TODO: CLEAN EDIT QUESTION DIALOG

@Component
export default class EditQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  editQuestion: Question = new Question(this.question);

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
    (this.$refs.form as Vue & { validate: () => boolean }).validate();

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

  addOption() {
    (this.editQuestion.questionDetailsDto as MultipleChoiceQuestionDetails).options.push(new Option());
  }

  removeOption(index: number) {
    (this.editQuestion.questionDetailsDto as MultipleChoiceQuestionDetails).options.splice(index, 1);
  }
}
</script>
