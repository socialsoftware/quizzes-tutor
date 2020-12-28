<template>
  <div>CodeOrderView</div>
</template>

<script lang="ts">
import CodeOrderAnswerDetails from '@/models/management/questions/CodeOrderAnswerDetails';
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';
import { Prop, Vue } from 'vue-property-decorator';
export default class CodeOrderView extends Vue {
  @Prop() readonly questionDetails!: CodeOrderQuestionDetails;
  @Prop() readonly answerDetails?: CodeOrderAnswerDetails;
}
</script>

<style lang="scss"></style>
