<template>
  <div class="questionDetails-container" v-if="questionDetails">
    <div class="code-container" style="position: relative; text-align: left">
      <v-overlay :model-value="!CodemirrorUpdated" contained color="white" opacity="1">
        <v-progress-circular indeterminate size="40" color="primary" />
      </v-overlay>
      <BaseCodeEditor
        v-model:code="questionDetails.code"
        v-model:language="questionDetails.language"
        :editable="false"
        :customExtensions="customExtensions"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import CodeFillInStatementQuestionDetails from '@/models/statement/questions/CodeFillInStatementQuestionDetails';
import CodeFillInStatementAnswerDetails from '@/models/statement/questions/CodeFillInStatementAnswerDetails';
import CodeFillInStatementCorrectAnswerDetails from '@/models/statement/questions/CodeFillInStatementCorrectAnswerDetails';
import StatementFillInSpot from '@/models/statement/questions/CodeFillInSpotStatement';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import {
  WidgetType,
  Decoration,
  MatchDecorator,
  ViewPlugin,
} from '@codemirror/view';

const props = defineProps<{
  questionOrder?: number;
  questionDetails: CodeFillInStatementQuestionDetails;
  answerDetails: CodeFillInStatementAnswerDetails;
  correctAnswerDetails: CodeFillInStatementCorrectAnswerDetails;
}>();

defineEmits([
  'update:questionOrder',
  'update:answerDetails',
  'increaseOrder',
  'decreaseOrder'
]);


const CodemirrorUpdated = ref(false);

// Loading overlay timing (decorations themselves apply instantly via the
// editor); mirrors the Vue2 refresh delay.
watch(
  () => props.questionDetails,
  () => {
    CodemirrorUpdated.value = false;
    setTimeout(() => {
      CodemirrorUpdated.value = true;
    }, 500);
  },
  { immediate: true, deep: true }
);


// Replaces {{slot-N}} placeholders with result <select> widgets managed by
// the editor itself. The previous implementation walked the rendered DOM and
// spliced nodes by hand, which breaks under CodeMirror 6: syntax
// highlighting splits slot text across text nodes and the editor wipes
// hand-inserted DOM on re-render, so options never appeared in the solution.
class ResultSelectWidget extends WidgetType {
  constructor(
    public slotNumber: number,
    public questionDetailsRef: any,
    public answerDetailsRef: any,
    public correctAnswerDetailsRef: any
  ) {
    super();
  }

  eq(other: ResultSelectWidget) {
    return this.slotNumber === other.slotNumber;
  }

  toDOM() {
    const num = this.slotNumber;
    const d = document.createElement('select');
    d.className = 'code-dropdown';
    d.name = 'slot-' + num;

    const something: StatementFillInSpot =
      this.questionDetailsRef.fillInSpots.find(
        (el: any) => el.sequence === num
      ) || new StatementFillInSpot();
    const optionAnswered = this.answerDetailsRef.selectedOptions?.find(
      (el: any) => el.sequence === num
    );
    const optionAnsweredQuestion =
      optionAnswered &&
      something.options.find(
        (el: any) => el.optionId === optionAnswered?.optionId
      );
    const correctOption =
      this.correctAnswerDetailsRef.correctOptions.find(
        (el: any) => el.sequence === num
      );
    const correctOptionQuestion = something.options.find(
      (el: any) => el.optionId === correctOption?.optionId
    );

    const answeredCorrectly =
      !!optionAnswered &&
      optionAnswered.optionId === correctOption?.optionId;

    if (!answeredCorrectly) {
      const correctOptEl = document.createElement('option');
      correctOptEl.innerHTML =
        ' ✔: ' + (correctOptionQuestion?.content || '');
      correctOptEl.classList.add('answerDetailsSynced-spot', 'correct');
      d.appendChild(correctOptEl);
    }

    something.options.forEach((element: any) => {
      if (
        !(
          element.optionId === optionAnswered?.optionId ||
          element.optionId === correctOption?.optionId
        )
      ) {
        const newOption = document.createElement('option');
        newOption.innerHTML = element?.content || '';
        newOption.classList.add('answerDetailsSynced-spot');
        d.appendChild(newOption);
      }
    });

    const option = document.createElement('option');
    option.innerHTML =
      (answeredCorrectly ? ' ✔: ' : ' ✖: ') +
      (optionAnsweredQuestion
        ? optionAnsweredQuestion.content
        : 'Not Answered');
    option.classList.add(
      'answerDetailsSynced-spot',
      answeredCorrectly ? 'correct' : 'incorrect'
    );
    d.prepend(option);
    d.selectedIndex = 0;

    // Prevent mousedown from blurring the editor and intercepting click
    d.onmousedown = (e) => e.stopPropagation();

    return d;
  }
}

const customExtensions = computed(() => {
  // Read lengths so decorations recompute when data arrives/changes
  const _selected = props.answerDetails?.selectedOptions?.length;
  const _spots = props.questionDetails?.fillInSpots?.length;
  const _correct = props.correctAnswerDetails?.correctOptions?.length;

  const slotMatcher = new MatchDecorator({
    regexp: /\{\{slot-(\d+)\}\}/g,
    decoration: (match) =>
      Decoration.replace({
        widget: new ResultSelectWidget(
          Number(match[1]),
          props.questionDetails,
          props.answerDetails,
          props.correctAnswerDetails
        ),
      }),
  });

  const slotPlugin = ViewPlugin.fromClass(
    class {
      decorations: any;
      constructor(view: any) {
        this.decorations = slotMatcher.createDeco(view);
      }
      update(update: any) {
        this.decorations = slotMatcher.updateDeco(update, this.decorations);
      }
    },
    {
      decorations: (v) => v.decorations,
    }
  );

  return [slotPlugin];
});
</script>

<style lang="scss">
.code-container {
  .code-dropdown {
    border-radius: 0px;
    border-width: 0 0 1px 0;
    border-style: solid;
    border-color: rgb(169, 169, 169);
    font-size: 0.8rem;
    padding: 0;
    -webkit-appearance: auto;
    -moz-appearance: auto;
  }
  .answerDetailsSynced-spot,
  .code-dropdown {
    border: solid 1px black;
    &.correct {
      background-color: #299455;
      color: white;
    }
    &.incorrect {
      background-color: #cf2323;
      color: white;
    }
  }
}
</style>
