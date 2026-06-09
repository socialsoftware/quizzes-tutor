<template>
  <v-dialog
    :model-value="dialog"
    max-width="75%"
    @update:model-value="$emit('update:dialog', $event)"
    @keydown.esc="$emit('update:dialog', false)"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Student View</span>
      </v-card-title>
      <v-card-text class="text-left">
        <div
          v-if="statementQuestion && statementQuestion.questionDetails && statementAnswerDetails"
          class="question-container"
        >
          <div class="question">
            <span class="square"> 1 </span>
            <div
              class="question-content"
              v-html="
                convertMarkDown(
                  statementQuestion.content || '',
                  statementQuestion.image
                )
              "
            ></div>
            <div class="square"></div>
          </div>
          <component
            :is="componentMap[statementQuestion.questionDetails.type]"
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
          class="text-white"
          data-cy="closeButton"
          @click="$emit('update:dialog', false)"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import StatementQuestion from '@/models/statement/StatementQuestion';
import MultipleChoiceAnswer from '@/components/multiple-choice/MultipleChoiceAnswer.vue';
import CodeFillInAnswer from '@/components/code-fill-in/CodeFillInAnswer.vue';
import CodeOrderAnswer from '@/components/code-order/CodeOrderAnswer.vue';
import Image from '@/models/management/Image';
import { convertMarkDown as convertMarkDownService } from '@/services/ConvertMarkdownService';
import { QuestionFactory } from '@/services/QuestionHelpers';
import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';

const props = defineProps<{
  dialog: boolean;
  statementQuestion: StatementQuestion;
}>();

defineEmits(['update:dialog']);

const statementAnswerDetails = ref<StatementAnswerDetails | null>(null);

const componentMap: Record<string, any> = {
  multiple_choice: MultipleChoiceAnswer,
  code_fill_in: CodeFillInAnswer,
  code_order: CodeOrderAnswer,
};

onMounted(() => {
  statementAnswerDetails.value = QuestionFactory.getFactory(
    props.statementQuestion.questionDetails.type
  ).createEmptyStatementAnswerDetails();
});

const convertMarkDown = (text: string, image: Image | null = null): string => {
  return convertMarkDownService(text, image);
};
</script>
