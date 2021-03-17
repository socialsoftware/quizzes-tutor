<template>
  <div class="base-code-editor" style="position: relative">
    <v-overlay :value="!CodemirrorUpdated" absolute color="white" opacity="1">
      <v-progress-circular indeterminate size="40" color="primary" />
    </v-overlay>
    <codemirror ref="myCm" v-model="syncedCode" :options="cmOptions" />
  </div>
</template>

<script lang="ts">
import {
  Component,
  Vue,
  Watch,
  Prop,
  PropSync,
  Emit,
} from 'vue-property-decorator';

import { codemirror } from 'vue-codemirror';
import CodeMirror from 'codemirror';

// codemirror plugins - check more @ https://codemirror.net/
import 'codemirror/mode/clike/clike.js';
import 'codemirror/mode/javascript/javascript.js';
import 'codemirror/mode/python/python.js';
import 'codemirror/addon/mode/overlay.js';
import 'codemirror/addon/scroll/simplescrollbars.js';
import { Dictionary } from 'vue-router/types/router';

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
      while (stream.next() != null && !stream.match('{{', false)) {}
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
export default class BaseCodeEditor extends Vue {
  @PropSync('code', { type: String, required: true }) syncedCode!: string;
  @PropSync('language', { type: String, default: 'Java' })
  syncedLanguage!: string;

  @Prop({ default: true })
  readonly editable!: boolean;

  @Prop({ default: false })
  readonly simple!: boolean;

  counter: number = 1;
  CodemirrorUpdated: boolean = false;
  static languagesDict: Dictionary<string> = {
    Java: 'text/x-java',
    Javascript: 'text/javascript',
    Python: 'text/x-python',
    CSharp: 'text/x-csharp',
  };
  @Watch('language')
  onLanguageUpdate() {}

  static get availableLanguages(): String[] {
    return Object.keys(this.languagesDict);
  }

  get codemirror(): CodeMirror.Editor {
    return (this.$refs.myCm as any).codemirror;
  }
  get cmOptions(): CodeMirror.EditorConfiguration {
    return {
      tabSize: 4,
      mode: { name: 'mustache', backdrop: this.languageCode },
      theme: 'eclipse',
      lineNumbers: !this.simple,
      dragDrop: false,
      readOnly: !this.editable,
      scrollbarStyle: 'overlay',
    };
  }
  get languageCode() {
    return BaseCodeEditor.languagesDict[this.syncedLanguage];
  }
  created() {
    if (!this.simple) {
      this.updateQuestion();
    }
    this.CodemirrorUpdated = true;
  }
  onCmCodeChange(newCode: string) {
    this.syncedCode = newCode;
  }

  updateQuestion() {
    this.CodemirrorUpdated = false;
    setTimeout(() => {
      this.codemirror.refresh();
      this.CodemirrorUpdated = true;
    }, 1000);
  }
}
</script>

<style>
@import '~codemirror/lib/codemirror.css';
@import '~codemirror/theme/eclipse.css';
@import '~codemirror/addon/scroll/simplescrollbars.css';
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
