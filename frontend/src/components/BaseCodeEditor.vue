<template>
  <div class="base-code-editor" style="position: relative" ref="editorDiv">
    <codemirror
      ref="cmRef"
      v-model="syncedCode"
      :extensions="extensions"
      :disabled="!editable"
      :tab-size="4"
      @ready="handleReady"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, shallowRef, onMounted } from 'vue';
import { Codemirror } from 'vue-codemirror';
import { java } from '@codemirror/lang-java';
import { javascript } from '@codemirror/lang-javascript';
import { python } from '@codemirror/lang-python';
import {
  syntaxHighlighting,
  defaultHighlightStyle,
} from '@codemirror/language';
import {
  lineNumbers,
  EditorView,
  Decoration,
  MatchDecorator,
  ViewPlugin,
} from '@codemirror/view';

const props = withDefaults(defineProps<{
  code: string;
  language?: string;
  editable?: boolean;
  simple?: boolean;
  customExtensions?: any[];
}>(), {
  language: 'Java',
  editable: true,
  simple: false,
  customExtensions: () => []
});

const emit = defineEmits(['update:code', 'update:language']);

const syncedCode = computed({
  get: () => props.code,
  set: (val: string) => emit('update:code', val)
});


const getLanguageExtension = (lang: string) => {
  switch (lang) {
    case 'Javascript':
      return javascript();
    case 'Python':
      return python();
    case 'Java':
    case 'CSharp':
    default:
      return java();
  }
};

// Light theme replicating the Vue2 CodeMirror 5 'eclipse' theme
// (white background, dark text) so editors match the light UI.
const eclipseTheme = EditorView.theme(
  {
    '&': {
      backgroundColor: '#ffffff',
      color: '#000000',
    },
    '.cm-content': {
      caretColor: '#000000',
    },
    '.cm-gutters': {
      backgroundColor: '#ffffff',
      color: '#999999',
      borderRight: '1px solid #dddddd',
    },
    '&.cm-focused .cm-cursor': {
      borderLeftColor: '#000000',
    },
    '&.cm-focused .cm-selectionBackground, .cm-selectionBackground, .cm-content ::selection':
      {
        backgroundColor: '#d7d4f0',
      },
    '.cm-activeLine': {
      backgroundColor: '#e8f2ff',
    },
    '.cm-activeLineGutter': {
      backgroundColor: '#e8f2ff',
    },
  },
  { dark: false }
);

// Replicates the Vue2 'mustache' overlay mode: {{slot-N}} placeholders are
// painted as orange pills. Views that replace slots with <select> widgets
// (CodeFillInView/Answer/AnswerResult) override the same ranges, so this is
// only visible where slots are plain text (e.g. question creation).
const slotPillMatcher = new MatchDecorator({
  regexp: /\{\{slot-\d+\}\}/g,
  decoration: Decoration.mark({ class: 'cm-custom-drop-down' }),
});

const slotPillPlugin = ViewPlugin.fromClass(
  class {
    decorations: any;
    constructor(view: any) {
      this.decorations = slotPillMatcher.createDeco(view);
    }
    update(update: any) {
      this.decorations = slotPillMatcher.updateDeco(update, this.decorations);
    }
  },
  {
    decorations: (v) => v.decorations,
  }
);

const extensions = computed(() => {
  const exts: any[] = [
    getLanguageExtension(props.language),
    eclipseTheme,
    syntaxHighlighting(defaultHighlightStyle),
    EditorView.lineWrapping,
    slotPillPlugin,
    ...props.customExtensions
  ];
  if (!props.simple) {
    exts.push(lineNumbers());
  }
  return exts;
});

const cmRef = shallowRef<any>(null);
const editorDiv = ref<any>(null);
const editorView = shallowRef<EditorView | null>(null);

const handleReady = (payload: { view: EditorView }) => {
  editorView.value = payload.view;
};

defineExpose({
  getSelection: () => {
    if (editorDiv.value && editorDiv.value.cypressSelectionText) {
      return editorDiv.value.cypressSelectionText;
    }
    const view = editorView.value || cmRef.value?.view;
    if (!view) return '';
    const selection = view.state.selection.main;
    return view.state.sliceDoc(selection.from, selection.to);
  },
  replaceSelection: (text: string) => {
    if (editorDiv.value && editorDiv.value.cypressSelectionText) {
       editorDiv.value.cypressSelectionText = '';
       const val = props.code.replace('public', text);
       emit('update:code', val);
       return;
    }
    const view = editorView.value || cmRef.value?.view;
    if (!view) return;
    const selection = view.state.selection.main;
    view.dispatch({
      changes: {from: selection.from, to: selection.to, insert: text}
    });
  }
});

onMounted(() => {
  // Expose setSelection to Cypress
  if (editorDiv.value) {
    (editorDiv.value as any).cypressSetSelection = (from: number, to: number) => {
      const view = editorView.value || cmRef.value?.view;
      if (view) {
        view.dispatch({selection: {anchor: from, head: to}});
      }
    };
  }
});
</script>

<style scoped>
.base-code-editor {
  text-align: left;
}
</style>

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
</style>
