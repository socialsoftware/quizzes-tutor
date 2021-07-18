<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Student View</span>
      </v-card-title>
      <v-card-text class="text-left">
        <div
          class="question-container"
          v-if="statementQuestion && statementQuestion.questionDetails"
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
            :questionDetails="statementQuestion.questionDetails"
            :answerDetails="statementAnswerDetails"
          >
          </component>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
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
