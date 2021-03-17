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
import CodeFillInSpotStatement from '@/models/statement/questions/CodeFillInSpotStatement';
import CodeFillInStatementAnswerDetails from '@/models/statement/questions/CodeFillInStatementAnswerDetails';
import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/clike/clike.js';
import 'codemirror/theme/eclipse.css';
import 'codemirror/theme/monokai.css';
import 'codemirror/addon/mode/overlay.js';
import CodeMirror from 'codemirror';
import { codemirror } from 'vue-codemirror';
import CodeFillInStatementCorrectAnswerDetails from '@/models/statement/questions/CodeFillInStatementCorrectAnswerDetails';
import StatementFillInSpot from '@/models/statement/questions/CodeFillInSpotStatement';

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
  @Prop(CodeFillInStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: CodeFillInStatementCorrectAnswerDetails;

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
    function getOptions(
      name: number,
      options: CodeFillInSpotStatement[]
    ): StatementFillInSpot {
      const result = options.find((el) => el.sequence === name);
      return result || new StatementFillInSpot();
    }
    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.name = e.innerHTML;
      e.parentNode?.replaceChild(d, e);
      const match = e.innerHTML.match(/\d+/);
      var num = Number(match ? match[0] : 0);
      const option = document.createElement('option');
      var something = getOptions(Number(num), this.questionDetails.fillInSpots);
      var optionAnswered = this.answerDetailsSynced.selectedOptions.find(
        (el) => el.sequence === num
      );
      var optionAnsweredQuestion =
        optionAnswered &&
        something.options.find(
          (el) => el.optionId === optionAnswered?.optionId
        );
      var correctOption = this.correctAnswerDetails.correctOptions.find(
        (el) => el.sequence === num
      );
      var correctOptionQuestion = something.options.find(
        (el) => el.optionId === correctOption?.optionId
      );
      var text;
      if (
        optionAnswered &&
        optionAnswered.optionId === correctOption?.optionId
      ) {
        text = ' ✔: ';
      } else {
        text = ' ✖: ';
        const correctOption = document.createElement('option');
        correctOption.innerHTML = ' ✔: ' + correctOptionQuestion?.content;
        correctOption.classList.add('answerDetailsSynced-spot', 'correct');
        d.appendChild(correctOption);
      }

      something.options.forEach((element) => {
        if (
          !(
            element.optionId === optionAnswered?.optionId ||
            element.optionId === correctOption?.optionId
          )
        ) {
          const newOption = document.createElement('option');
          newOption.innerHTML = element?.content;
          newOption.classList.add('answerDetailsSynced-spot');
          d.appendChild(newOption);
        }
      });

      option.innerHTML =
        text +
        (optionAnsweredQuestion
          ? optionAnsweredQuestion.content
          : 'Not Answered');
      option.classList.add(
        'answerDetailsSynced-spot',
        optionAnswered && optionAnswered.optionId === correctOption?.optionId
          ? 'correct'
          : 'incorrect'
      );
      d.prepend(option);
      d.selectedIndex = 0;
    });
  }
}
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
