<template>
  <div id="ViewCodeMirror">
    <BaseCodeEditor
      ref="myCmView"
      v-model:code="questionDetails.code"
      v-model:language="questionDetails.language"
      :editable="false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import CodeFillInQuestionDetails from '@/models/management/questions/CodeFillInQuestionDetails';
import CodeFillInAnswerDetails from '@/models/management/questions/CodeFillInAnswerDetails';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';
import Option from '@/models/management/Option';

const props = defineProps<{
  questionDetails: CodeFillInQuestionDetails;
  answerDetails?: CodeFillInAnswerDetails;
}>();

const myCmView = ref<any>(null);
const CodemirrorUpdated = ref(false);

const convertMarkDownFn = (text: string, image: Image | null = null): string => {
  return convertMarkDown(text, image);
};

const studentAnswered = (option: Option): boolean => {
  return (
    (props.answerDetails &&
      props.answerDetails.options.some((x) => x.id === option.id)) ||
    false
  );
};

const createOptionChild = (option: Option, index: number) => {
  const o = document.createElement('option');
  o.appendChild(
    document.createTextNode(
      (studentAnswered(option) ? ' S - ' : '') +
        (option.correct ? ' ✔: ' : ' ✖: ') +
        option.content
    )
  );
  o.value = option.id?.toString() || index.toString();
  return o;
};

const addOptions = (select: HTMLSelectElement, options: Option[]) => {
  options.forEach((opt: Option, i: number) => {
    const optionEl = createOptionChild(opt, i);
    if (studentAnswered(opt)) {
      select.prepend(optionEl);
    } else {
      select.appendChild(optionEl);
    }
  });
};

const getOptions = (name: number, options: CodeFillInSpot[]): Option[] => {
  const result = options.find((el) => el.sequence === name);
  return result?.options || [];
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

  if (!myCmView.value) return;
  const el = myCmView.value.$el;

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

      addOptions(
        d,
        getOptions(num, props.questionDetails.fillInSpots)
      );
      d.selectedIndex = 0;

      if (studentAnswered) {
        let isCorrect = false;
        let isAnswered = false;
        const opts = getOptions(num, props.questionDetails.fillInSpots);
        for (const opt of opts) {
          if (studentAnswered(opt)) {
            isAnswered = true;
            if (opt.correct) isCorrect = true;
          }
        }
        if (isAnswered) {
          d.classList.add(isCorrect ? 'correct' : 'incorrect');
        }
      }

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

const updateQuestion = () => {
  replaceDropdowns();
  CodemirrorUpdated.value = true;
  document.body.addEventListener(
    'mousedown',
    function (evt: Event) {
      const htmlTarget = evt?.target as HTMLElement;
      if (htmlTarget?.className === 'code-dropdown') {
        evt.stopPropagation();
      }
    },
    true
  );
};

const refreshQuestion = () => {
  CodemirrorUpdated.value = false;
  setTimeout(() => {
    updateQuestion();
  }, 1000);
};

watch(() => props.questionDetails, () => {
  if (CodemirrorUpdated.value) {
    refreshQuestion();
  }
}, { immediate: false, deep: true });

onMounted(() => {
  refreshQuestion();
});
</script>

<style>
#ViewCodeMirror select.code-dropdown.incorrect {
  background-color: rgba(187, 36, 36, 0.76);
}
#ViewCodeMirror select.code-dropdown.correct {
  background-color: rgba(33, 201, 33, 0.76);
}
#ViewCodeMirror select.code-dropdown {
  background-color: transparent;
  color: inherit;
  border-radius: 0px;
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgb(169, 169, 169);
  padding: 0;
}
#ViewCodeMirror select.code-dropdown option {
  color: #272822;
}
#ViewCodeMirror .CodeMirror {
  border: 1px solid #eee;
  height: auto;
}
</style>
