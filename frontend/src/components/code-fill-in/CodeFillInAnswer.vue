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
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import CodeFillInSpotStatement from '@/models/statement/questions/CodeFillInSpotStatement';
import CodeFillInStatementAnswerDetails from '@/models/statement/questions/CodeFillInStatementAnswerDetails';
import CodeFillInSpotAnswerStatement from '@/models/statement/questions/CodeFillInSpotAnswerStatement';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';

const props = defineProps<{
  questionOrder?: number;
  questionDetails: CodeFillInStatementQuestionDetails;
  answerDetails: CodeFillInStatementAnswerDetails;
  questionNumber?: number;
  backsies?: boolean;
}>();

const emit = defineEmits([
  'question-answer-update',
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

const convertMarkDownFn = (text: string, image: Image | null = null): string => {
  return convertMarkDown(text, image);
};

const selectedANewOption = (event: Event) => {
  const num = Number(((event.target as any).name.match(/\d+/) || [0])[0]);
  const selectIndex = (event.target as any).selectedIndex - 1;
  const dataQuestion = props.questionDetails.fillInSpots.find(
    (el) => el.sequence === num
  );
  const data = answerDetailsSynced.value.selectedOptions?.find(
    (el: any) => el.sequence === num
  );
  if (data) {
    data.optionId = dataQuestion?.options[selectIndex]?.optionId;
  } else {
    const newData = new CodeFillInSpotAnswerStatement();
    newData.optionId = dataQuestion?.options[selectIndex]?.optionId;
    newData.sequence = num;
    answerDetailsSynced.value.selectedOptions.push(newData);
  }
  emit('question-answer-update');
};

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

  const getOptions = (name: number, options: CodeFillInSpotStatement[]) => {
    return options.find((el) => el.sequence === name);
  };
  
  const addOptions = (select: HTMLSelectElement, options: any) => {
    const data = answerDetailsSynced.value.selectedOptions?.find(
      (el: any) => el.sequence === options.sequence
    );
    const blankO = document.createElement('option');
    blankO.innerHTML = '-- select an option --';
    if (!data) blankO.setAttribute('selected', '');
    blankO.setAttribute('value', '');
    select.appendChild(blankO);
    
    options.options.forEach((opt: any, i: number) => {
      const o = document.createElement('option');
      o.appendChild(document.createTextNode(opt.content));
      o.value = String(i);
      if (data && data.optionId == opt.optionId) {
        o.setAttribute('selected', '');
      }
      select.appendChild(o);
    });
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
      const num = match[1];
      const before = text.substring(lastIndex, match.index);
      if (before) fragment.appendChild(document.createTextNode(before));
      
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.onchange = selectedANewOption;
      d.name = 'slot-' + num;
      
      const something = getOptions(
        Number(num),
        props.questionDetails.fillInSpots
      );
      if (something) addOptions(d, something);
      
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

<style>
.code-container select.code-dropdown {
  background-color: #e0e2e5;
  color: inherit;
  border-radius: 0px;
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgb(169, 169, 169);
  font-size: 0.8rem;
  padding: 0;
  -webkit-appearance: auto;
  -moz-appearance: auto;
}
.code-container select.code-dropdown option {
  color: #272822;
}
.code-container .CodeMirror {
  border: 1px solid #eee;
  height: auto;
}
</style>
