<template>
  <div class="questionDetails-container" v-if="questionDetails">
    <div class="code-container" style="position: relative; text-align: left">
      <BaseCodeEditor
        ref="myCmStudent"
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
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import CodeFillInSpotStatement from '@/models/statement/questions/CodeFillInSpotStatement';
import CodeFillInStatementAnswerDetails from '@/models/statement/questions/CodeFillInStatementAnswerDetails';
import CodeFillInSpotAnswerStatement from '@/models/statement/questions/CodeFillInSpotAnswerStatement';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import { WidgetType, Decoration, MatchDecorator, ViewPlugin } from '@codemirror/view';

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

class SelectWidget extends WidgetType {
  constructor(public slotNumber: number, public propsRef: any, public answerDetailsRef: any) {
    super();
  }

  eq(other: SelectWidget) {
    // If the selected option id changes, we want the widget to re-render
    const thisData = this.answerDetailsRef.value.selectedOptions?.find((el: any) => el.sequence === this.slotNumber);
    const otherData = other.answerDetailsRef.value.selectedOptions?.find((el: any) => el.sequence === other.slotNumber);
    return this.slotNumber === other.slotNumber && thisData?.optionId === otherData?.optionId;
  }

  toDOM() {
    const d = document.createElement('select');
    d.className = 'code-dropdown';
    d.name = 'slot-' + this.slotNumber;
    d.onchange = selectedANewOption;

    const something = this.propsRef.questionDetails.fillInSpots.find(
      (el: any) => el.sequence === this.slotNumber
    );

    if (something) {
      const data = this.answerDetailsRef.value.selectedOptions?.find(
        (el: any) => el.sequence === this.slotNumber
      );
      const blankO = document.createElement('option');
      blankO.innerHTML = '-- select an option --';
      if (!data) blankO.setAttribute('selected', '');
      blankO.setAttribute('value', '');
      d.appendChild(blankO);
      
      something.options.forEach((opt: any, i: number) => {
        const o = document.createElement('option');
        o.appendChild(document.createTextNode(opt.content));
        o.value = String(i);
        if (data && data.optionId == opt.optionId) {
          o.setAttribute('selected', '');
        }
        d.appendChild(o);
      });
    }

    // Prevent mousedown from blurring the editor and intercepting click
    d.onmousedown = (e) => e.stopPropagation();

    return d;
  }
}

const customExtensions = computed(() => {
  // Read length/options to ensure reactivity when answers change
  const _ = answerDetailsSynced.value?.selectedOptions?.map((o: any) => o.optionId);

  const slotMatcher = new MatchDecorator({
    regexp: /\{\{slot-(\d+)\}\}/g,
    decoration: (match) => Decoration.replace({
      widget: new SelectWidget(Number(match[1]), props, answerDetailsSynced)
    })
  });

  const slotPlugin = ViewPlugin.fromClass(class {
    decorations: any;
    constructor(view: any) {
      this.decorations = slotMatcher.createDeco(view);
    }
    update(update: any) {
      this.decorations = slotMatcher.updateDeco(update, this.decorations);
    }
  }, {
    decorations: v => v.decorations
  });

  return [slotPlugin];
});
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
.code-container .cm-editor {
  border: 1px solid #eee;
  height: auto;
}
</style>
