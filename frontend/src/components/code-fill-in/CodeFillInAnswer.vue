<template>
  <div class="questionDetails-container" v-if="questionDetails">
    <div class="code-container" style="position: relative; text-align: left">
      <v-overlay :value="!CodemirrorUpdated" absolute color="white" opacity="1">
        <v-progress-circular indeterminate size="40" color="primary" />
      </v-overlay>
      <codemirror
        ref="myCmStudent"
        :value="questionDetails.code"
        :options="cmOptions"
      >
      </codemirror>
    </div>
  </div>
</template>

<script lang="ts">
import {
  Component,
  Vue,
  Prop,
  Model,
  Emit,
  Watch,
  PropSync,
} from 'vue-property-decorator';
import CodeFillInStatementQuestionDetails from '@/models/statement/questions/CodeFillInStatementQuestionDetails';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import CodeFillInSpotStatement from '@/models/statement/questions/CodeFillInSpotStatement';
import CodeFillInStatementAnswerDetails from '@/models/statement/questions/CodeFillInStatementAnswerDetails';
import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/clike/clike.js';
import 'codemirror/theme/eclipse.css';
import 'codemirror/theme/monokai.css';
import 'codemirror/addon/mode/overlay.js';
import CodeMirror from 'codemirror';
import { codemirror } from 'vue-codemirror';
import CodeFillInSpotAnswerStatement from '@/models/statement/questions/CodeFillInSpotAnswerStatement';

CodeMirror.defineMode('mustache', function (config: any, parserConfig: any) {
  const mustacheOverlay = {
    token: function (stream: any) {
      let ch;
      if (stream.match('{{slot-')) {
        while ((ch = stream.next()) != null) {
          if (ch === '}' && stream.next() === '}') {
            stream.eat('}');
            return 'custom-drop-down';
          }
        }
      }
      while (stream.next() != null && !stream.match('{{', false)) {
        // empty
      }
      return null;
    },
  };
  return CodeMirror.overlayMode(
    CodeMirror.getMode(config, parserConfig.backdrop || 'text/x-java'),
    mustacheOverlay
  );
});

@Component({
  components: {
    codemirror,
  },
})
export default class CodeFillInAnswer extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(CodeFillInStatementQuestionDetails)
  readonly questionDetails!: CodeFillInStatementQuestionDetails;
  @PropSync('answerDetails', CodeFillInStatementAnswerDetails)
  answerDetailsSynced!: CodeFillInStatementAnswerDetails;
  @Prop() readonly questionNumber!: number;
  @Prop() readonly backsies!: boolean;
  hover: boolean = false;
  cmOptions: any = {
    // codemirror options
    tabSize: 4,
    mode: 'mustache',
    theme: 'eclipse',
    lineNumbers: true,
    line: true,
    readOnly: true,
  };
  CodemirrorUpdated: boolean = false;
  @Emit()
  increaseOrder() {
    return 1;
  }
  @Emit()
  decreaseOrder() {
    return 1;
  }

  @Watch('questionDetails', { immediate: true, deep: true })
  updateQuestion() {
    this.CodemirrorUpdated = false;
    setTimeout(() => {
      (this.$refs.myCmStudent as any).codemirror.refresh();
      this.replaceDropdowns();
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
      this.CodemirrorUpdated = true;
    }, 100);
  }
  replaceDropdowns() {
    var that = this;
    function shuffle(arr: any) {
      const array = arr.slice();
      let currentIndex = array.length;
      let temporaryValue;
      let randomIndex;
      while (currentIndex !== 0) {
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;
        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
      }
      return array;
    }
    function createOptionChild(
      optText: any,
      index: any,
      selected: boolean | undefined
    ) {
      const o = document.createElement('option');
      o.appendChild(document.createTextNode(optText.content));
      o.value = index;
      if (selected) {
        o.setAttribute('selected', '');
      }
      return o;
    }
    function creatBlankOptionChild(selected: boolean) {
      const o = document.createElement('option');
      o.innerHTML = '-- select an option --';
      if (selected) {
        o.setAttribute('selected', '');
      }
      o.setAttribute('value', '');
      return o;
    }
    function addOptions(select: any, options: any) {
      const data = that.answerDetailsSynced.selectedOptions?.find(
        (el) => el.sequence === options.sequence
      );
      select.appendChild(creatBlankOptionChild(!data));
      options.options.forEach((opt: any, i: any) => {
        select.appendChild(
          createOptionChild(opt, i, data && data.optionId == opt.optionId)
        );
      });
    }
    function getOptions(name: number, options: CodeFillInSpotStatement[]) {
      const result = options.find((el) => el.sequence === name);
      return result;
    }
    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.onchange = this.selectedANewOption;
      d.name = e.innerHTML;
      e.parentNode?.replaceChild(d, e);
      let match = e.innerHTML.match(/\d+/);
      var num = match ? match[0] : 0;
      var something = getOptions(Number(num), this.questionDetails.fillInSpots);
      addOptions(d, something);
    });
  }
  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
  selectedANewOption(event: Event) {
    var num = Number((event.target as any).name.match(/\d+/)[0]);
    var selectIndex = (event.target as any).selectedIndex - 1;
    var dataQuestion = this.questionDetails.fillInSpots.find(
      (el) => el.sequence === num
    );
    var data = this.answerDetailsSynced.selectedOptions?.find(
      (el) => el.sequence === num
    );
    if (data) {
      data.optionId = dataQuestion?.options[selectIndex]?.optionId;
    } else {
      var newData = new CodeFillInSpotAnswerStatement();
      newData.optionId = dataQuestion?.options[selectIndex]?.optionId;
      newData.sequence = num;
      this.answerDetailsSynced.selectedOptions.push(newData);
    }
    this.$emit('question-answer-update');
  }
}
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
