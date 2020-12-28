<template>
  <div>CodeOrderAnswer</div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import CodeOrderStatementQuestionDetails from '@/models/statement/questions/CodeOrderStatementQuestionDetails';
import CodeOrderStatementAnswerDetails from '@/models/statement/questions/CodeOrderStatementAnswerDetails';
import CodeOrderStatementCorrectAnswerDetails from '@/models/statement/questions/CodeOrderStatementCorrectAnswerDetails';

@Component
export default class CodeOrderAnswer extends Vue {
  @Prop(CodeOrderStatementQuestionDetails)
  readonly questionDetails!: CodeOrderStatementQuestionDetails;
  @Prop(CodeOrderStatementAnswerDetails)
  answerDetails!: CodeOrderStatementAnswerDetails;
  @Prop(CodeOrderStatementCorrectAnswerDetails)
  readonly correctAnswerDetails?: CodeOrderStatementCorrectAnswerDetails;
}
</script>

<style lang="scss"></style>
