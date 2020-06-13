<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card data-cy="showQuestionDialog">
      <v-card-title>
        <span class="headline">{{ question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-multiple-choice-question 
          v-if="question.type === 'multiple_choice'" 
          :question="question" />
        <show-code-fill-in-question 
          v-if="question.type === 'code_fill_in'" 
          :question="question" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';
import ShowCodeFillInQuestion from '@/views/teacher/questions/ShowCodeFillInQuestion.vue';

@Component({
  components: {
    'show-multiple-choice-question': ShowQuestion,
    'show-code-fill-in-question': ShowCodeFillInQuestion
  }
})
export default class ShowQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;
}
</script>
