<template>
  <div>CodeOrderCreate</div>
</template>

<script lang="ts">
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';
import { Component, Vue, Prop, Watch, PropSync } from 'vue-property-decorator';

export default class CodeOrderCreate extends Vue {
  @PropSync('questionDetails', { type: CodeOrderQuestionDetails })
  sQuestionDetails!: CodeOrderQuestionDetails;
}
</script>

<style lang="scss"></style>
