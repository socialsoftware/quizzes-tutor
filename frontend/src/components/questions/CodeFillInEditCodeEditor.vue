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
          <span>
            Select code on the editor and click here to create a fillable space.
          </span>
        </v-tooltip>
      </v-card-actions>
      <BaseCodeComponent
        ref='codeEditor'
        :code.sync="question.code"
        :language.sync="question.language"
      />

      <FillInOptions
        v-for="(item, index) in question.fillInSpots"
        :key="index"
        :option="item"
        v-model="question.fillInSpots[index]"
      />
      <!-- v-on:change="handleInput" -->
    </div>
  </v-card-text>
</template>

<script lang="ts">
import { Component, Vue, Watch, Prop, PropSync } from 'vue-property-decorator';
import CodeFillInQuestion from '@/models/management/code-fill-in/CodeFillInQuestion';
import Question from '../../models/management/Question';
import FillInOptions from '@/components/questions/FillInOptions.vue';
import Option from '@/models/management/Option';
import CodeFillInSpot from '@/models/management/code-fill-in/CodeFillInSpot';

import BaseCodeComponent from '@/components/questions/BaseCodeEditor.vue';
import BaseCodeEditor from '@/components/questions/BaseCodeEditor.vue';

@Component({
  components: {
    BaseCodeComponent,
    FillInOptions
  }
})
export default class CreateOrEditMultipleChoice extends Vue {
  @PropSync('value', { type: CodeFillInQuestion }) question!: CodeFillInQuestion;

  languages: Array<string> = ['Java', 'Javascript'];
  counter: number = 1;
 
  get baseCodeEditorRef() : BaseCodeEditor{
    return this.$refs.codeEditor as BaseCodeEditor;
  }

  created() {
    this.counter = this.getMaxDropdown()
  }

  getMaxDropdown() {
      return (Math.max.apply(Math, this.question.fillInSpots.map(function(o) { return o.sequence; })) | 0) + 1;
  }

  onCmCodeChange(newCode: string) {
    this.question.code = newCode;
  }

  Dropdownify() {
    const content = this.baseCodeEditorRef.codemirror.getSelection();
    if (content) {
      const option = new Option();
      option.correct = true;
      option.content = content;
      const item = new CodeFillInSpot();
      item.options = [option];
      item.sequence = this.counter;

      this.question.fillInSpots.push(item);
      this.baseCodeEditorRef.codemirror.replaceSelection(
        '{{slot-' + this.counter + '}}'
      );
      this.counter++;
    }
  }
}
</script>

<style>
.cm-custom-drop-down {
  background: #ffa014;
  color: white;
  font-size: x-small;
  padding: 4px 2px 4px 2px;
  border-radius: 5px;
  font-weight: bolder;
  height: 16px;
}

.code-create {
  text-align: left;
}

.CodeMirror-linenumber.CodeMirror-gutter-elt {
  left: 0;
}
</style>
