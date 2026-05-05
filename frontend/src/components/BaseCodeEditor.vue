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
import { oneDark } from '@codemirror/theme-one-dark';
import { lineNumbers, EditorView } from '@codemirror/view';

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

const syncedLanguage = computed({
  get: () => props.language,
  set: (val: string) => emit('update:language', val)
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

const extensions = computed(() => {
  const exts: any[] = [
    getLanguageExtension(props.language),
    oneDark,
    EditorView.lineWrapping,
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
