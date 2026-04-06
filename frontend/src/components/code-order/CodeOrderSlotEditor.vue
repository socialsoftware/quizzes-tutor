<template>
  <div class="code-order-create-slot">
    <i class="fa fa-align-justify handle"></i>
    <div>
      {{ sQuestionSlot.order == null ? null : sQuestionSlot.order + 1 }}
    </div>
    <BaseCodeEditor
      class="slot-content"
      ref="codeEditor"
      v-model:code="sQuestionSlot.content"
      :language="language"
    />
    <div class="toolbar">
      <v-btn icon>
        <v-icon
          v-if="sQuestionSlot.order == null"
          @click="$emit('add-order')"
          color="grey lighten-1"
          >mdi-checkbox-blank-outline
        </v-icon>
        <v-icon
          v-if="sQuestionSlot.order != null"
          @click="$emit('remove-order')"
          color="green lighten-1"
          >mdi-checkbox-marked-outline</v-icon
        >
      </v-btn>
      <v-btn v-if="canDelete" @click="$emit('delete-row')" icon>
        <v-icon color="red lighten-1">mdi-delete-forever </v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import CodeOrderSlot from '@/models/management/questions/CodeOrderSlot';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  questionSlot: CodeOrderSlot;
  canDelete?: boolean;
  language?: string;
}>(), {
  canDelete: false
});

const emit = defineEmits(['update:questionSlot', 'add-order', 'remove-order', 'delete-row']);

const sQuestionSlot = computed({
  get: () => props.questionSlot,
  set: (val) => emit('update:questionSlot', val)
});
</script>

<style lang="scss">
.code-order-create-slot {
  background-color: white;
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin: 10px 10px;

  & > * {
    min-width: 30px;
    margin: auto;
  }

  & > .slot-content {
    flex-grow: 1;

    & .cm-editor {
      max-height: 150px !important;
      height: 150px !important;
    }
  }

  & > .toolbar {
    display: inline-flex;
    flex-direction: column;
    justify-content: center;
  }
}
</style>
