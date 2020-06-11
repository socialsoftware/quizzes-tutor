<template>
  <div class="question-container" v-if="question">
    <div class="question">
      <span
        v-if="backsies"
        class="square"
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div class="square" @click="increaseOrder">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <div style="position:relative; text-align:left">
      <v-overlay :value="!CodemirrorUpdated" absolute color="white" opacity="1">
        <v-progress-circular indeterminate size="40" color="primary" />
      </v-overlay>
      <codemirror ref="myCmStudent" :value="question.code" :options="cmOptions">
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
  PropSync
} from 'vue-property-decorator';
import StatementQuestionCodeFillIn from '@/models/statement/StatementQuestionCodeFillIn';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementFillInSpot from '../../../models/statement/StatementFillInSpot';
import StatementAnswerCodeFillIn from '../../../models/statement/StatementAnswerCodeFillIn';

import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/clike/clike.js';
//
import 'codemirror/theme/eclipse.css';
import 'codemirror/theme/monokai.css';
import 'codemirror/addon/mode/overlay.js';
import CodeMirror from 'codemirror';
import { codemirror } from 'vue-codemirror';
import StatementAnswerCodeFillInOption from '../../../models/statement/StatementAnswerCodeFillInOption';

CodeMirror.defineMode('mustache', function(config: any, parserConfig: any) {
  const mustacheOverlay = {
    token: function(stream: any) {
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
    }
  };
  return CodeMirror.overlayMode(
    CodeMirror.getMode(config, parserConfig.backdrop || 'text/x-java'),
    mustacheOverlay
  );
});

@Component({
  components: {
    codemirror
  }
})
export default class QuestionComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestionCodeFillIn) readonly question:
    | StatementQuestionCodeFillIn
    | undefined;
  @PropSync('answer', StatementAnswerCodeFillIn) answerData: StatementAnswerCodeFillIn | undefined;
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
    readOnly: true
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

  // @Emit()
  // selectOption(optionId: number) {
  //   return optionId;
  // }

  @Watch('question', { immediate: true, deep: true })
  updateQuestion() {
    this.CodemirrorUpdated = false;
    setTimeout(() => {
      this.$refs.myCmStudent.codemirror.refresh();
      this.replaceDropdowns();
      document.body.addEventListener(
        'mousedown',
        function(evt: Event) {
          if (evt && evt.target && evt.target.className === 'code-dropdown') {
            evt.stopPropagation();
          }
        },
        true
      );
      this.CodemirrorUpdated = true;
    }, 500);
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

    function createOptionChild(optText: any, index: any, selected: boolean | undefined) {
      const o = document.createElement('option');
      o.appendChild(document.createTextNode(optText.content));
      o.value = index;
      if (selected){
        o.setAttribute("selected","");
      }
      return o;
    }

     function creatBlankOptionChild(selected: boolean) {
      const o = document.createElement('option');
      o.innerHTML =  "-- select an option --";
      o.setAttribute("disabled","");
      if(selected){
        o.setAttribute("selected","");
      }
      o.setAttribute("value","");
      o.setAttribute("hidden","");
      return o;
    }

    function addOptions(select: any, options: any) {
      console.log(that.answerData.selectedOptions);
      const data = that.answerData.selectedOptions.find(el => el.sequence === options.sequence);
      select.appendChild(creatBlankOptionChild(!data))
      options.options.forEach((opt: any, i: any) => {
        select.appendChild(createOptionChild(opt, i, data && data.optionId == opt.optionId));
      });
    }

    function getOptions(name: number, options: StatementFillInSpot[]) {
      const result = options.find(el => el.sequence === name);
      return result;
    }

    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      console.log("ok");
      d.onchange = this.selectedANewOption;
      d.name = e.innerHTML;
      e.parentNode.replaceChild(d, e);
      var num = e.innerHTML.match(/\d+/)[0];
      var something = getOptions(Number(num), this.question.fillInSpots);
      addOptions(d, something);
    });
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  selectedANewOption(event: Event){
    var num = Number(event.target.name.match(/\d+/)[0]);
    var selectIndex = event.target.selectedIndex - 1;
    var dataQuestion = this.question.fillInSpots.find(el => el.sequence === num);
    var data = this.answerData.selectedOptions.find(el => el.sequence === num);
    if(data){
      data.optionId = dataQuestion.options[selectIndex].optionId;
    }else{
      var newData = new StatementAnswerCodeFillInOption();
      newData.optionId = dataQuestion.options[selectIndex].optionId;
      newData.sequence = num;
      this.answerData.selectedOptions.push(newData);
    }
    console.log(dataQuestion.options[selectIndex].content);
    this.$emit("select-option");
  }
}
</script>

<style lang="scss" scoped />
<style>

.question-container select.code-dropdown {
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

.question-container select.code-dropdown option {
  color: #272822;
}

.question-container .CodeMirror {
  border: 1px solid #eee;
  height: auto;
}
</style>
