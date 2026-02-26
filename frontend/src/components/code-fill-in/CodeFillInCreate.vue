<template>
  <div>
    <v-select
      :items="languages"
      v-model="sQuestionDetails.language"
      label="Language"
      :disabled="readonlyEdit"
    />
    <div class="code-create">
      <v-card-actions>
        <v-spacer />
        <v-tooltip top>
          <template v-slot:activator="{ props }">
            <v-btn color="primary" small @click="Dropdownify" v-bind="props">
              Answer Slot
            </v-btn>
          </template>
          <span>
            Select code on the editor and click here to create a fillable space.
          </span>
        </v-tooltip>
      </v-card-actions>

      <BaseCodeEditor
        ref="codeEditor"
        v-model:code="sQuestionDetails.code"
        v-model:language="sQuestionDetails.language"
      />

      <FillInOptions
        v-for="(item, index) in sQuestionDetails.fillInSpots"
        :key="index"
        :option="item"
        v-model="sQuestionDetails.fillInSpots[index]"
      />
      <!-- v-on:change="handleInput" -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import CodeFillInQuestionDetails from '@/models/management/questions/CodeFillInQuestionDetails';
import FillInOptions from '@/components/code-fill-in/CodeFillInOptions.vue';
import Option from '@/models/management/Option';
import CodeFillInSpot from '@/models/management/questions/CodeFillInSpot';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';

const props = withDefaults(defineProps<{
  questionDetails: CodeFillInQuestionDetails;
  readonlyEdit?: boolean;
}>(), {
  readonlyEdit: true
});

const emit = defineEmits(['update:questionDetails']);

const sQuestionDetails = computed({
  get: () => props.questionDetails,
  set: (val) => emit('update:questionDetails', val),
});

const counter = ref<number>(1);
const codeEditor = ref<any>(null);

const languages = computed(() => ['Java', 'Javascript', 'Python', 'CSharp']);

const getMaxDropdown = () => {
  return (
    (Math.max.apply(
      Math,
      sQuestionDetails.value.fillInSpots.map(function (o) {
        return o.sequence || 0;
      })
    ) | 0) + 1
  );
};

onMounted(() => {
  counter.value = getMaxDropdown();
});

const onCmCodeChange = (newCode: string) => {
  sQuestionDetails.value.code = newCode;
};

const Dropdownify = () => {
  // Note: CM6 editor access might need updating in BaseCodeEditor
  const content = codeEditor.value?.getSelection?.();
  if (content) {
    const option = new Option();
    option.correct = true;
    option.content = content;
    const item = new CodeFillInSpot();
    item.options = [option];
    item.sequence = counter.value;
    sQuestionDetails.value.fillInSpots.push(item);
    codeEditor.value?.replaceSelection?.(
      '{{slot-' + counter.value + '}}'
    );
    counter.value++;
  }
};
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
