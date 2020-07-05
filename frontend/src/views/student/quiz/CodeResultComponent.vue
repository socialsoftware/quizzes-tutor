<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      answer.isCorrect(correctAnswer)
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <div class="question">
      <span
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
        class="square"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div @click="increaseOrder" class="square">
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
  Watch
} from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import StatementCorrectAnswerCodeFillIn from '@/models/statement/code-fill-in/StatementCorrectAnswerCodeFillIn';
import StatementQuestionCodeFillIn from '@/models/statement/code-fill-in/StatementQuestionCodeFillIn';

import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/clike/clike.js';
//
import 'codemirror/theme/eclipse.css';
import 'codemirror/theme/monokai.css';
import 'codemirror/addon/mode/overlay.js';
import CodeMirror from 'codemirror';
import { codemirror } from 'vue-codemirror';
import StatementAnswerCodeFillInOption from '@/models/statement/code-fill-in/StatementAnswerCodeFillInOption';
import StatementFillInSpot from '@/models/statement/StatementFillInSpot';
import StatementAnswerCodeFillIn from '@/models/statement/code-fill-in/StatementAnswerCodeFillIn';

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
export default class ResultComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestionCodeFillIn)
  readonly question!: StatementQuestionCodeFillIn;
  @Prop(StatementCorrectAnswerCodeFillIn)
  readonly correctAnswer!: StatementCorrectAnswerCodeFillIn;
  @Prop(StatementAnswerCodeFillIn) readonly answer!: StatementAnswerCodeFillIn;
  @Prop() readonly questionNumber!: number;
  hover: boolean = false;

  CodemirrorUpdated: boolean = false;

  cmOptions: any = {
    // codemirror options
    tabSize: 4,
    mode: 'mustache',
    theme: 'eclipse',
    lineNumbers: true,
    line: true,
    readOnly: true
  };

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

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
    }, 100);
  }

  replaceDropdowns() {
      function getOptions(
      name: number,
      options: StatementFillInSpot[]
    ): StatementFillInSpot {
      const result = options.find(el => el.sequence === name);
      return result;
    }

    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      const d = document.createElement('span');
      d.className = 'code-dropdown';
      e.parentNode.replaceChild(d, e);
      var num = Number(e.innerHTML.match(/\d+/)[0]);
      console.log(this.answer);
      console.log(this.correctAnswer);
      var something = getOptions(Number(num), this.question.fillInSpots);
      var optionAnswered = this.answer.selectedOptions.find(
        el => el.sequence === num
      );
      var optionAnsweredQuestion = optionAnswered && something.options.find(
        el => el.optionId === optionAnswered.optionId
      );
      var correctOption = this.correctAnswer.correctOptions.find(
        el => el.sequence === num
      );
      var correctOptionQuestion = something.options.find(
        el => el.optionId === correctOption.optionId
      );
      var text = optionAnswered && optionAnswered.optionId === correctOption.optionId ? ' ✔: ' : ' ✖: '
      d.innerHTML = text + (optionAnsweredQuestion ? optionAnsweredQuestion.content : 'Not Answered');
      d.classList.add(
        'answer-spot',
        optionAnswered && optionAnswered.optionId === correctOption.optionId
          ? 'correct'
          : 'incorrect'
      );
    });
  }
}
</script>

<style lang="scss">
.answer-spot {
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
</style>
<style lang="scss" scoped>

.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .question .question-content {
    background-color: #285f23 !important;
    color: white !important;
  }
  .question .square {
    background-color: #285f23 !important;
    color: white !important;
  }
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.incorrect-question {
  .question .question-content {
    background-color: #761515 !important;
    color: white !important;
  }
  .question .square {
    background-color: #761515 !important;
    color: white !important;
  }
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>
