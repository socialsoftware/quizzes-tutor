<template>
  <v-card-text>
    <v-select
      :items="languages"
      v-model="question.language"
      label="Language"
      :disabled="value.id != null"
    />
    <div class="code-create">
      <v-card-actions>
        <v-spacer />
        <v-tooltip top>
          <template v-slot:activator="{ on }">
            <v-btn color="primary" small @click="Dropdownify" v-on="on">
              Answer Slot
            </v-btn>
          </template>
          <span
            >Select code on the editor and click here to create a fillable
            space.</span
          >
        </v-tooltip>
      </v-card-actions>
      <codemirror
        ref="myCm"
        :value="question.code"
        :options="cmOptions"
        @input="onCmCodeChange"
      >

      </codemirror>
      <FillInOptions
        v-for="(item, index) in question.codeFillInSpots"
        :key="index"
        :option="item"
        v-model="question.codeFillInSpots[index]"
      />
              <!-- v-on:change="handleInput" -->

    </div>
  </v-card-text>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import CodeFillInQuestion from '@/models/management/questions/CodeFillInQuestion';
import Question from '../../models/management/Question';
import { codemirror } from 'vue-codemirror';
import CodeMirror from 'codemirror';
import FillInOptions from '@/components/questions/FillInOptions';
import Option from '@/models/management/Option';

import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/clike/clike.js'
//
import 'codemirror/theme/eclipse.css'
import 'codemirror/addon/mode/overlay.js'
import CodeFillInSpot from '../../models/management/questions/CodeFillInSpot';


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
    codemirror,
    FillInOptions
  }
})
export default class CreateOrEditMultipleChoice extends Vue {
  @PropSync('value', { type: CodeFillInQuestion }) question!: CodeFillInQuestion;

  languages: Array<string> = ['Java', 'Javascript'];
  counter: number = 1;
  cmOptions: any = {
    // codemirror options
    tabSize: 4,
    mode: 'mustache',
    theme: 'eclipse',
    lineNumbers: true,
    line: true
  };

  onCmCodeChange (newCode : string) {
      this.question.code = newCode;
  }

  Dropdownify() {
    const content = this.$refs.myCm.codemirror.getSelection();
    if (content) {
      const option = new Option();
      option.correct = true;
      option.content = content;
      const item = new CodeFillInSpot();
      item.options = [option];
      item.sequence = this.counter;
      
      this.question.codeFillInSpots.push(item);
      this.$refs.myCm.codemirror.replaceSelection("{{slot-"+this.counter+"}}");
      this.counter++;
    }
  }
}
</script>

<style>
.cm-custom-drop-down {
  background: #FFA014;
  color: white;
  font-size: x-small;
  padding: 4px 2px 4px 2px;
  border-radius: 5px;
  font-weight: bolder;
  height: 16px;
}

.code-create{
  text-align: left;
}
</style>
