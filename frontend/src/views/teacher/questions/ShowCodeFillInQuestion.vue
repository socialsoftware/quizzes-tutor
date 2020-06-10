<template>
  <div id="ViewCodeMirror">
    <span v-html="convertMarkDown(question.content, question.image)" />
    <br />
    <div style="position:relative">
      <v-overlay :value="!CodemirrorUpdated" absolute color="white" opacity="1">
        <v-progress-circular indeterminate size="40" color="primary" />
      </v-overlay>
      <codemirror ref="myCm" :value="question.code" :options="cmOptions">
      </codemirror>
    </div>
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import CodeFillInQuestion from '@/models/management/questions/CodeFillInQuestion';

import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/clike/clike.js';
//
import 'codemirror/theme/eclipse.css';
import 'codemirror/theme/monokai.css';
import 'codemirror/addon/mode/overlay.js';
import CodeMirror from 'codemirror';
import { codemirror } from 'vue-codemirror';
import FillInOptions from '../../../components/questions/FillInOptions.vue';
import CodeFillInSpot from '../../../models/management/questions/CodeFillInSpot';

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
export default class ShowCodeFillInQuestion extends Vue {
  @Prop({ type: CodeFillInQuestion, required: true })
  readonly question!: CodeFillInQuestion;

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

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  replaceDropdowns() {
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

    function createOptionChild(optText: any, index: any) {
      const o = document.createElement('option');
      o.appendChild(
        document.createTextNode(
          (optText.correct ? ' ✔: ' : ' ✖: ') + optText.content
        )
      );
      o.value = index;
      return o;
    }

    function addOptions(select: any, options: any) {
      console.log(options);
      options.forEach((opt: any, i: any) => {
        select.appendChild(createOptionChild(opt, i));
      });
    }

    // function getOptions(name: number, options: CodeFillInSpot[]) {
    //   const result = options.find(el => el.sequence === name);
    //   return result ? result.options : result;
    // }

    document.querySelectorAll('.cm-custom-drop-down').forEach((e, index) => {
      console.log(e.innerHTML);
      const d = document.createElement('select');
      d.className = 'code-dropdown';
      d.name = e.innerHTML;
      console.log(e.parentNode);
      console.log(d);
      e.parentNode.replaceChild(d, e);
      addOptions(d, this.question.fillInSpots[index].options);
    });
  }

  @Watch('question', { immediate: true, deep: true })
  updateQuestion() {
    this.CodemirrorUpdated = false;
    setTimeout(() => {
      this.$refs.myCm.codemirror.refresh();
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

  mounted() {
    this.updateQuestion();
  }

  // updated() {
  //   console.log('updated');
  //   setTimeout(() => {
  //     this.$refs.myCm.codemirror.refresh();
  //     this.replaceDropdowns();
  //     document.body.addEventListener(
  //       'mousedown',
  //       function(evt: Event) {
  //         if (evt && evt.target && evt.target.className === 'code-dropdown') {
  //           evt.stopPropagation();
  //         }
  //       },
  //       true
  //     );
  //   }, 200);
  // }
}
</script>

<style>
select.code-dropdown.incorrect {
  background-color: rgba(187, 36, 36, 0.76);
}

select.code-dropdown.correct {
  background-color: rgba(33, 201, 33, 0.76);
}

select.code-dropdown {
  background-color: transparent;
  color: inherit;
  border-radius: 0px;
  border-width: 0 0 1px 0;
  border-style: solid;
  border-color: rgb(169, 169, 169);
  font-size: 0.8rem;
  padding: 0;
}

select.code-dropdown option {
  color: #272822;
}
</style>
