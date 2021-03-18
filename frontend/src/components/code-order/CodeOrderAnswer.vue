<template>
  <div class="code-order-answer">
    <draggable
      class="code-order-answer-options"
      tag="ul"
      :list="questionDetails.orderSlots"
      :group="{ name: 'answer', pull: 'clone', put: false }"
      :clone="cloneAnswerFromQuestion"
      draggable="li.dragable"
      :sort="false"
    >
      <h4 class="code-order-header" slot="header">
        Answer Options:
        <p class="question-warning">
          All options might used or only part of them.
        </p>
      </h4>
      <li
        v-for="(el, index) in questionDetails.orderSlots"
        :key="index"
        :class="{
          dragable: !answerDetails.orderedSlots.find((x) => x.slotId == el.id),
        }"
      >
        <i class="fa fa-align-justify handle"></i>
        <BaseCodeEditor
          class="content"
          ref="codeEditor"
          :code.sync="el.content"
          :language.sync="questionDetails.language"
          :editable="false"
          :simple="true"
        />
      </li>
    </draggable>
    <draggable
      class="code-order-answer-response"
      v-model="answerList"
      group="answer"
      draggable="li"
    >
      <h4 class="code-order-header" slot="header">Reponse:</h4>
      <li
        v-for="(el, index) in answerDetails.orderedSlots"
        :key="index"
        class="dragable"
      >
        <i class="fa fa-align-justify handle"></i>
        <BaseCodeEditor
          class="content"
          ref="codeEditor"
          :code.sync="
            questionDetails.orderSlots.find((x) => x.id == el.slotId).content
          "
          :language.sync="questionDetails.language"
          :editable="false"
          :simple="true"
        />
        <v-btn @click="removeAnswer(index)" icon small>
          <v-icon color="red lighten-1">mdi-playlist-remove </v-icon>
        </v-btn>
      </li>
    </draggable>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue } from 'vue-property-decorator';
import CodeOrderStatementQuestionDetails from '@/models/statement/questions/CodeOrderStatementQuestionDetails';
import CodeOrderStatementAnswerDetails from '@/models/statement/questions/CodeOrderStatementAnswerDetails';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import draggable from 'vuedraggable';
import CodeOrderSlotStatementQuestionDetails from '@/models/statement/questions/CodeOrderSlotStatementQuestionDetails';
import CodeOrderSlotStatementAnswerDetails from '@/models/statement/questions/CodeOrderSlotStatementAnswerDetails';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';

@Component({
  components: {
    BaseCodeEditor,
    draggable,
  },
})
export default class CodeOrderAnswer extends Vue {
  @Prop(CodeOrderStatementQuestionDetails)
  readonly questionDetails!: CodeOrderStatementQuestionDetails;
  @Prop(CodeOrderStatementAnswerDetails)
  answerDetails!: CodeOrderStatementAnswerDetails;

  get answerList() {
    return this.answerDetails.orderedSlots;
  }

  set answerList(value) {
    this.answerDetails.orderedSlots = value;
    this.updateAnswer();
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  cloneAnswerFromQuestion(element: CodeOrderSlotStatementQuestionDetails) {
    return new CodeOrderSlotStatementAnswerDetails({
      slotId: element.id,
      order: null,
    });
  }

  removeAnswer(index: number) {
    this.answerDetails.orderedSlots.splice(index, 1);
    this.updateAnswer();
  }

  updateAnswer() {
    this.answerDetails.orderedSlots.forEach(
      (element: CodeOrderSlotStatementAnswerDetails, index: number) => {
        element.order = index;
      }
    );
    this.$emit('question-answer-update');
  }
}
</script>

<style lang="scss">
.code-order-answer {
  background-color: white;
  display: inline-flex;
  justify-content: space-between;
  align-items: stretch;
  width: 100%;
  position: relative;

  & .code-order-header {
    padding: 10px;
    margin: 0px;

    .question-warning {
      padding: 0;
      margin: 0;
      font-weight: 100;
      font-size: smaller;
    }
  }

  & li {
    padding: 10px;
    margin: 5px 0;
    border: 2px solid rgb(202, 202, 202);
    display: flex;
    justify-content: space-between;
    align-items: center;

    &:not(.dragable) {
      opacity: 0.6;
    }

    & > .content {
      flex-grow: 1;
      max-width: 95%;

      & .CodeMirror {
        height: auto;
      }
    }

    & > .content > *:last-child {
      margin-bottom: 0;
    }
  }

  & > *:not(.question-warning) {
    list-style: none;
    text-align: left;
    flex-grow: 1;
    padding-left: 0 !important;
    width: 50%;
    border: dashed 1px gray;
  }

  & > .code-order-answer-response {
    background-color: rgb(241, 241, 241);

    & > .content {
      flex-grow: 1;
      max-width: 90%;
    }
  }
}
</style>
