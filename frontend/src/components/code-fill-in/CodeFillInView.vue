<template>
  <div id="ViewCodeMirror">
    <BaseCodeEditor
      ref="myCmView"
      :code.sync="questionDetails.code"
      :language.sync="questionDetails.language"
      :editable="false"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import CodeFillInQuestionDetails from '@/models/management/questions/CodeFillInQuestionDetails';
import CodeFillInAnswerDetails from '@/models/management/questions/CodeFillInAnswerDetails';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';
import Option from '@/models/management/Option';

@Component({
  components: {
    BaseCodeEditor,
  },
})
export default class ShowCodeFillInQuestion extends Vue {
  @Prop({ type: CodeFillInQuestionDetails, required: true })
  readonly questionDetails!: CodeFillInQuestionDetails;
  @Prop() readonly answerDetails?: CodeFillInAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  get baseCodeEditorInstance(): BaseCodeEditor {
    return this.$refs.myCmView as BaseCodeEditor;
  }

  studentAnswered(option: Option): boolean {
    return (
      (this.answerDetails &&
        this.answerDetails.options.some((x) => x.id === option.id)) ||
      false
    );
  }

  createOptionChild(option: Option, index: number) {
    const o = document.createElement('option');
    o.appendChild(
      document.createTextNode(
        (this.studentAnswered(option) ? ' S - ' : '') +
          (option.correct ? ' ✔: ' : ' ✖: ') +
          option.content
      )
    );
    o.value = option.id?.toString() || index.toString();
    return o;
  }

  addOptions(select: HTMLSelectElement, options: Option[]) {
    options.forEach((opt: Option, i: number) => {
      let option = this.createOptionChild(opt, i);
      if (this.studentAnswered(opt)) {
        select.prepend(option);
      } else {
        select.appendChild(option);
      }
    });
  }

  getOptions(name: number, options: CodeFillInSpot[]): Option[] {
    const result = options.find((el) => el.sequence === name);
    return result?.options || [];
  }

  getSlotNumber(html: string): number {
    const num = html.match(/\d+/);
    return Number(num);
  }

  replaceDropdowns() {
    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.name = e.innerHTML;
      e.parentNode?.replaceChild(d, e);
      this.addOptions(
        d,
        this.getOptions(
          this.getSlotNumber(e.innerHTML),
          this.questionDetails.fillInSpots
        )
      );
      d.selectedIndex = 0;
    });
  }

  updateQuestion() {
    this.baseCodeEditorInstance.codemirror.refresh();
    this.replaceDropdowns();
    this.baseCodeEditorInstance.CodemirrorUpdated = true;
    document.body.addEventListener(
      'mousedown',
      function (evt: Event) {
        let htmlTarget = evt?.target as HTMLElement;
        if (htmlTarget?.className === 'code-dropdown') {
          evt.stopPropagation();
        }
      },
      true
    );
  }

  @Watch('questionDetails', { immediate: false, deep: true })
  updateOnQuestionChange() {
    if (this.baseCodeEditorInstance.CodemirrorUpdated) {
      this.refreshQuestion();
    }
  }

  refreshQuestion() {
    this.baseCodeEditorInstance.CodemirrorUpdated = false;
    setTimeout(() => {
      this.updateQuestion();
    }, 1000);
  }

  mounted() {
    this.refreshQuestion();
  }
}
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
