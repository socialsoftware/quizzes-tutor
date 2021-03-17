<template>
  <div class="question-container" v-if="question && question.questionDetails">
    <div class="question">
      <span
        v-if="backsies"
        class="square"
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div class="square" @click="increaseOrder">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <component
      :is="question.questionDetails.type"
      :questionDetails="question.questionDetails"
      :answerDetails="answer.answerDetails"
      v-on="$listeners"
    >
    </component>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import StatementQuestion from '@/models/statement/StatementQuestion';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import MultipleChoiceAnswer from '@/components/multiple-choice/MultipleChoiceAnswer.vue';
import StatementAnswer from '@/models/statement/StatementAnswer';
import CodeFillInAnswer from '@/components/code-fill-in/CodeFillInAnswer.vue';
import CodeOrderAnswer from '@/components/code-order/CodeOrderAnswer.vue';

@Component({
  components: {
    multiple_choice: MultipleChoiceAnswer,
    code_fill_in: CodeFillInAnswer,
    code_order: CodeOrderAnswer,
  },
})
export default class QuestionComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestion) readonly question: StatementQuestion | undefined;
  @Prop(StatementAnswer) answer: StatementAnswer | undefined;
  @Prop() readonly questionNumber!: number;
  @Prop() readonly backsies!: boolean;
  hover: boolean = false;

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped />
