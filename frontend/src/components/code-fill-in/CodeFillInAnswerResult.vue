<template>
  <div class="questionDetails-container" v-if="questionDetails">
    <div class="code-container" style="position: relative; text-align: left">
      <v-overlay :model-value="!CodemirrorUpdated" contained color="white" opacity="1">
        <v-progress-circular indeterminate size="40" color="primary" />
      </v-overlay>
      <BaseCodeEditor
        ref="myCmStudent"
        v-model:code="questionDetails.code"
        v-model:language="questionDetails.language"
        :editable="false"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import CodeFillInStatementQuestionDetails from '@/models/statement/questions/CodeFillInStatementQuestionDetails';
import CodeFillInSpotStatement from '@/models/statement/questions/CodeFillInSpotStatement';
import CodeFillInStatementAnswerDetails from '@/models/statement/questions/CodeFillInStatementAnswerDetails';
import CodeFillInStatementCorrectAnswerDetails from '@/models/statement/questions/CodeFillInStatementCorrectAnswerDetails';
import StatementFillInSpot from '@/models/statement/questions/CodeFillInSpotStatement';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';

const props = defineProps<{
  questionOrder?: number;
  questionDetails: CodeFillInStatementQuestionDetails;
  answerDetails: CodeFillInStatementAnswerDetails;
  correctAnswerDetails: CodeFillInStatementCorrectAnswerDetails;
}>();

const emit = defineEmits([
  'update:questionOrder',
  'update:answerDetails',
  'increaseOrder',
  'decreaseOrder'
]);

const answerDetailsSynced = computed({
  get: () => props.answerDetails,
  set: (val) => emit('update:answerDetails', val)
});

const CodemirrorUpdated = ref(false);
const myCmStudent = ref<any>(null);

const increaseOrder = () => emit('increaseOrder', 1);
const decreaseOrder = () => emit('decreaseOrder', 1);

const replaceDropdowns = () => {
  const walkDOM = (node: Node, func: (node: Text) => void) => {
    if (node.nodeType === Node.TEXT_NODE) {
      if (node.nodeValue?.includes('{{slot-')) {
        func(node as Text);
      }
    } else {
      for (let i = 0; i < node.childNodes.length; i++) {
        walkDOM(node.childNodes[i], func);
      }
    }
  };

  const getOptions = (name: number, options: CodeFillInSpotStatement[]): StatementFillInSpot => {
    const result = options.find((el) => el.sequence === name);
    return result || new StatementFillInSpot();
  };

  if (!myCmStudent.value) return;
  const el = myCmStudent.value.$el;

  walkDOM(el, (textNode: Text) => {
    const text = textNode.nodeValue || '';
    const regex = /\{\{slot-(\d+)\}\}/g;
    let match;
    let lastIndex = 0;
    const parent = textNode.parentNode;
    if (!parent) return;

    if (parent.nodeName === 'SELECT' || parent.nodeName === 'OPTION') return;

    const fragment = document.createDocumentFragment();
    let found = false;

    while ((match = regex.exec(text)) !== null) {
      found = true;
      const num = Number(match[1]);
      const before = text.substring(lastIndex, match.index);
      if (before) fragment.appendChild(document.createTextNode(before));

      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.name = 'slot-' + num;

      const option = document.createElement('option');
      const something = getOptions(num, props.questionDetails.fillInSpots);
      const optionAnswered = answerDetailsSynced.value.selectedOptions?.find(
        (el: any) => el.sequence === num
      );
      const optionAnsweredQuestion = optionAnswered && something.options.find(
        (el) => el.optionId === optionAnswered?.optionId
      );
      const correctOption = props.correctAnswerDetails.correctOptions.find(
        (el) => el.sequence === num
      );
      const correctOptionQuestion = something.options.find(
        (el) => el.optionId === correctOption?.optionId
      );

      let displayText;
      if (optionAnswered && optionAnswered.optionId === correctOption?.optionId) {
        displayText = ' ✔: ';
      } else {
        displayText = ' ✖: ';
        const correctOptEl = document.createElement('option');
        correctOptEl.innerHTML = ' ✔: ' + (correctOptionQuestion?.content || '');
        correctOptEl.classList.add('answerDetailsSynced-spot', 'correct');
        d.appendChild(correctOptEl);
      }

      something.options.forEach((element) => {
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

      option.innerHTML =
        displayText +
        (optionAnsweredQuestion ? optionAnsweredQuestion.content : 'Not Answered');
      option.classList.add(
        'answerDetailsSynced-spot',
        optionAnswered && optionAnswered.optionId === correctOption?.optionId
          ? 'correct'
          : 'incorrect'
      );
      d.prepend(option);
      d.selectedIndex = 0;

      fragment.appendChild(d);
      lastIndex = regex.lastIndex;
    }

    if (found) {
      const after = text.substring(lastIndex);
      if (after) fragment.appendChild(document.createTextNode(after));
      parent.replaceChild(fragment, textNode);
    }
  });
};

watch(() => props.questionDetails, () => {
  CodemirrorUpdated.value = false;
  setTimeout(() => {
    replaceDropdowns();
    document.body.addEventListener(
      'mousedown',
      function (evt: Event) {
        if (
          evt &&
          evt.target &&
          (evt.target as any).className === 'code-dropdown'
        ) {
          evt.stopPropagation();
        }
      },
      true
    );
    CodemirrorUpdated.value = true;
  }, 500);
}, { immediate: true, deep: true });
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
