<template>
  <v-dialog
    :value="dialog"
    max-width="75%"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Student View</span>
      </v-card-title>
      <v-card-text class="text-left">
        <div
          v-if="statementQuestion && statementQuestion.questionDetails"
          class="question-container"
        >
          <div class="question">
            <span class="square"> 1 </span>
            <div
              class="question-content"
              v-html="
                convertMarkDown(
                  statementQuestion.content,
                  statementQuestion.image
                )
              "
            ></div>
            <div class="square"></div>
          </div>
          <component
            :is="statementQuestion.questionDetails.type"
            :answerDetails="statementAnswerDetails"
            :questionDetails="statementQuestion.questionDetails"
          >
          </component>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          dark
          data-cy="closeButton"
          @click="$emit('dialog')"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StatementQuestion from '@/models/statement/StatementQuestion';
import MultipleChoiceAnswer from '@/components/multiple-choice/MultipleChoiceAnswer.vue';
import CodeFillInAnswer from '@/components/code-fill-in/CodeFillInAnswer.vue';
import CodeOrderAnswer from '@/components/code-order/CodeOrderAnswer.vue';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import { QuestionFactory } from '@/services/QuestionHelpers';
import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';

@Component({
  components: {
    multiple_choice: MultipleChoiceAnswer,
    code_fill_in: CodeFillInAnswer,
    code_order: CodeOrderAnswer,
  },
})
export default class StudentViewDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StatementQuestion, required: true })
  readonly statementQuestion!: StatementQuestion;

  statementAnswerDetails!: StatementAnswerDetails;

  created() {
    this.statementAnswerDetails = QuestionFactory.getFactory(
      this.statementQuestion.questionDetails.type
    ).createEmptyStatementAnswerDetails();
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
