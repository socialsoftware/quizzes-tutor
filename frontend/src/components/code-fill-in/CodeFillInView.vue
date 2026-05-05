<template>
  <div id="ViewCodeMirror">
    <BaseCodeEditor
      ref="myCmView"
      v-model:code="questionDetails.code"
      v-model:language="questionDetails.language"
      :editable="false"
      :customExtensions="customExtensions"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import CodeFillInQuestionDetails from '@/models/management/questions/CodeFillInQuestionDetails';
import CodeFillInAnswerDetails from '@/models/management/questions/CodeFillInAnswerDetails';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';
import Option from '@/models/management/Option';
import { WidgetType, Decoration, MatchDecorator, ViewPlugin } from '@codemirror/view';

const props = defineProps<{
  questionDetails: CodeFillInQuestionDetails;
  answerDetails?: CodeFillInAnswerDetails;
}>();

const convertMarkDownFn = (text: string, image: Image | null = null): string => {
  return convertMarkDown(text, image);
};

class SelectWidget extends WidgetType {
  constructor(public slotNumber: number, public propsRef: any) {
    super();
  }

  eq(other: SelectWidget) {
    return this.slotNumber === other.slotNumber;
  }

  toDOM() {
    const d = document.createElement('select');
    d.className = 'code-dropdown';
    d.name = 'slot-' + this.slotNumber;
    
    const studentAnsweredLocal = (option: Option): boolean => {
      return (
        (this.propsRef.answerDetails &&
          this.propsRef.answerDetails.options.some((x: any) => x.id === option.id)) ||
        false
      );
    };

    const createOptionChildLocal = (option: Option, index: number) => {
      const o = document.createElement('option');
      o.appendChild(
        document.createTextNode(
          (studentAnsweredLocal(option) ? ' S - ' : '') +
            (option.correct ? ' ✔: ' : ' ✖: ') +
            option.content
        )
      );
      o.value = option.id?.toString() || index.toString();
      return o;
    };

    const addOptionsLocal = (select: HTMLSelectElement, options: Option[]) => {
      options.forEach((opt: Option, i: number) => {
        const optionEl = createOptionChildLocal(opt, i);
        if (studentAnsweredLocal(opt)) {
          select.prepend(optionEl);
        } else {
          select.appendChild(optionEl);
        }
      });
    };

    const getOptionsLocal = (name: number, options: CodeFillInSpot[]): Option[] => {
      const result = options.find((el: any) => el.sequence === name);
      return result?.options || [];
    };

    addOptionsLocal(
      d,
      getOptionsLocal(this.slotNumber, this.propsRef.questionDetails.fillInSpots)
    );
    d.selectedIndex = 0;

    let isCorrect = false;
    let isAnswered = false;
    const opts = getOptionsLocal(this.slotNumber, this.propsRef.questionDetails.fillInSpots);
    for (const opt of opts) {
      if (studentAnsweredLocal(opt)) {
        isAnswered = true;
        if (opt.correct) isCorrect = true;
      }
    }
    if (isAnswered) {
      d.classList.add(isCorrect ? 'correct' : 'incorrect');
    }

    return d;
  }
}

const customExtensions = computed(() => {
  const slotMatcher = new MatchDecorator({
    regexp: /\{\{slot-(\d+)\}\}/g,
    decoration: (match) => Decoration.replace({
      widget: new SelectWidget(Number(match[1]), props)
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
#ViewCodeMirror .cm-editor {
  border: 1px solid #eee;
  height: auto;
}
</style>
