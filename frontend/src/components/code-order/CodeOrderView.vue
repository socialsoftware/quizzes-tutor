<template>
  <ul class="code-order-view">
    <li
      v-for="el in questionDetails.codeOrderSlots"
      :key="el.id!"
      :class="{
        'not-used': el.order == null,
        student: !!answerDetails,
        correct: !!answerDetails && studentAnswerCorrect(el),
      }"
    >
      <b style="padding-right: 10px">{{
        el.order == null ? null : el.order + 1
      }}</b>
      <BaseCodeEditor
        class="slot-content"
        ref="codeEditor"
        v-model:code="el.content"
        v-model:language="questionDetails.language"
        :editable="false"
        :simple="true"
      />
      <div v-if="!answerDetails" v-html="el.order != null ? ' ✔ ' : ' ✖ '" />
      <div v-if="answerDetails" v-html="studentAnswer(el)" />
    </li>
  </ul>
</template>

<script setup lang="ts">
import CodeOrderAnswerDetails from '@/models/management/questions/CodeOrderAnswerDetails';
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';
import CodeOrderSlot from '@/models/management/questions/CodeOrderSlot';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';

const props = defineProps<{
  questionDetails: CodeOrderQuestionDetails;
  answerDetails?: CodeOrderAnswerDetails;
}>();

const studentAnswerCorrect = (el: CodeOrderSlot): boolean => {
  let answer = props.answerDetails?.orderedSlots.find(
    (x) => x.slotId == el.id
  );
  return !!answer ? answer.correct : el.order == null;
};

const studentAnswer = (el: CodeOrderSlot): string => {
  let answer = props.answerDetails?.orderedSlots.find(
    (x) => x.slotId == el.id
  );
  return `S[${!!answer ? (answer?.order || 0) + 1 : 'Not Used'}][${
    studentAnswerCorrect(el) ? '✔' : '✖'
  }]`;
};
</script>

<style lang="scss">
.code-order-view {
  padding-left: 0px !important;

  & > li {
    background-color: rgba(145, 252, 190, 0.507);
    display: inline-flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    list-style: none;
    padding: 5px;
    margin: 2px 0;
    border: 2px solid rgb(202, 202, 202);

    &.not-used,
    &.student:not(.correct) {
      background-color: rgb(187, 87, 87);
      color: white;
    }

    & > * {
      min-width: 30px;
      margin: auto;
      text-align: center;
    }

    & > .slot-content {
      flex-grow: 1;
      text-align: left;

      & .CodeMirror {
        height: auto;
      }

      & > p:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
