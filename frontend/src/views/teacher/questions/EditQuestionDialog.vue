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
              ? 'New Question'
              : 'Edit Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editQuestion">
        <v-text-field v-model="editQuestion.title" label="Title" />
        <v-textarea
          outline
          rows="10"
          v-model="editQuestion.content"
          label="Question"
        ></v-textarea>
        <div v-for="index in editQuestion.options.length" :key="index">
          <v-switch
            v-model="editQuestion.options[index - 1].correct"
            class="ma-4"
            label="Correct"
          />
          <v-textarea
            outline
            rows="10"
            v-model="editQuestion.options[index - 1].content"
            :label="`Option ${index}`"
          ></v-textarea>
        </div>
      </v-card-text>

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
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  editQuestion!: Question;

  created() {
    this.updateQuestion();
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
