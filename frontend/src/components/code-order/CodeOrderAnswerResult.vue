<template>
  <div class="code-order-answer-result">
    <div class="code-order-answer-student">
      <h4 class="code-order-header">Answer:</h4>
      <div
        v-for="(el, index) in answerDetails.orderedSlots"
        :key="index"
        :class="{correct: isCorrect(el, index)}"
      >
        <span class="order" v-html="el.order" />
        <span class="content" v-html="convertMarkDown(slotById(el.slotId).content)" />
        <span class="is-correct" v-html="isCorrect(el, index) ? ' ✔ ' :' ✖ '" />
      </div>
    </div>
    <div class="code-order-answer-correct">
      <h4 class="code-order-header">Correct Order:</h4>
      <div
        v-for="(el, index) in correctAnswerDetails.correctOrder"
        :key="index"
        :class="{'not-used': el.order == null}"
      >
        <span class="content" v-html="convertMarkDown(slotById(el.slotId).content)"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import CodeOrderSlotStatementAnswerDetails from '@/models/statement/questions/CodeOrderSlotStatementAnswerDetails';
import CodeOrderStatementAnswerDetails from '@/models/statement/questions/CodeOrderStatementAnswerDetails';
import CodeOrderStatementCorrectAnswerDetails from '@/models/statement/questions/CodeOrderStatementCorrectAnswerDetails';
import CodeOrderStatementQuestionDetails from '@/models/statement/questions/CodeOrderStatementQuestionDetails';
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';

@Component
export default class CodeOrderAnswerResult extends Vue {
  @Prop(CodeOrderStatementQuestionDetails)
  readonly questionDetails!: CodeOrderStatementQuestionDetails;
  @Prop(CodeOrderStatementAnswerDetails)
  readonly answerDetails!: CodeOrderStatementAnswerDetails;
  @Prop(CodeOrderStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: CodeOrderStatementCorrectAnswerDetails;

  slotById(slotId:number){
    return this.questionDetails.orderSlots.find(x => x.id == slotId);
  }

  isCorrect(element: CodeOrderSlotStatementAnswerDetails, index:number){
    let correctPlaced = this.correctAnswerDetails.correctOrder[index];
    return element.slotId == correctPlaced.slotId;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

}
</script>

<style lang="scss">
.code-order-answer-result {
  background-color: white;
  display: inline-flex;
  justify-content: space-between;
  align-items: stretch;
  width: 100%;
  position: relative;

  & .code-order-header {
    padding: 10px;
    margin: 0px 10px;
  }

  & > div {
    width: 100%;
  }

  & > div > div {
    padding: 10px;
    margin: 5px 0;
    border: 2px solid rgb(202, 202, 202);
  }

  & > .code-order-answer-student > div {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    &.correct {
      background-color: #299455;
      color: white;
    }
    &:not(.correct) {
      background-color: #cf2323;
      color: white;
    }
  }

  & .not-used {
    opacity: 0.6;
  }
}
</style>
