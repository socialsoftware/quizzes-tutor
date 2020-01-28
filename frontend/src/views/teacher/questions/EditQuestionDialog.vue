<template>
  <v-dialog v-model="dialog" max-width="75%">
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
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field v-model="editQuestion.title" label="Title" />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <vue-simplemde
                v-model="editQuestion.content"
                class="question-textarea"
                :configs="markdownConfigs"
              />
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in editQuestion.options.length"
              :key="index"
            >
              <v-switch
                v-model="editQuestion.options[index - 1].correct"
                class="ma-4"
                label="Correct"
              />
              <vue-simplemde
                v-model="editQuestion.options[index - 1].content"
                class="option-textarea"
                :configs="markdownConfigs"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="closeDialogue">Cancel</v-btn>
        <v-btn color="blue darken-1" @click="saveQuestion">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditQuestionDialog extends Vue {
  @Prop({ type: Question, required: true }) readonly question!: Question;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  editQuestion!: Question;

  created() {
    this.editQuestion = new Question(this.question);
  }

  // https://github.com/F-loat/vue-simplemde/blob/master/doc/configuration_en.md
  markdownConfigs: object = {
    status: false,
    spellChecker: false,
    insertTexts: {
      image: ['![image][image]', '']
    }
  };

  closeDialogue() {
    this.$emit('close-edit-question-dialog');
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

    if (this.editQuestion && this.editQuestion.id != null) {
      try {
        const result = await RemoteServices.updateQuestion(this.editQuestion);
        this.$emit('save-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editQuestion) {
      try {
        const result = await RemoteServices.createQuestion(this.editQuestion);
        this.$emit('save-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
