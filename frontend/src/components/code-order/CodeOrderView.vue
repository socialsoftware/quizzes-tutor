<template>
  <ul class="code-order-view">
    <li v-for="el in questionDetails.codeOrderSlots" :key="el.id" :class="{'not-used': el.order == null}">
      <b style="padding-right:10px">{{ el.order }}</b>
      <div class="slot-content" v-html="convertMarkDown(el.content)" />
      <div v-html="el.order != null? ' ✔ ' :' ✖ ' " />
    </li>
  </ul>
</template>

<script lang="ts">
import CodeOrderAnswerDetails from '@/models/management/questions/CodeOrderAnswerDetails';
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';

@Component
export default class CodeOrderView extends Vue {
  @Prop() readonly questionDetails!: CodeOrderQuestionDetails;
  @Prop() readonly answerDetails?: CodeOrderAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss">
.code-order-view {
  padding-left: 0px!important;

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

    &.not-used {
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

      & > p:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
