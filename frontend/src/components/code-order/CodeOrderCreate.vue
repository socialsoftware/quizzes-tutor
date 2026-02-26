<template>
  <div class="code-order-create">
    <v-select
      :items="languages"
      v-model="sQuestionDetails.language"
      label="Language"
      :disabled="readonlyEdit"
    />
    <v-card-actions>
      <v-spacer />
      <v-tooltip top>
        <template v-slot:activator="{ props }">
          <v-btn color="primary" small @click="newSlot" v-bind="props"
            >New Slot</v-btn
          >
        </template>
        <span> Click to add new slots for the order problem. </span>
      </v-tooltip>
    </v-card-actions>
    <draggable
      tag="div"
      @end="endedReorder()"
      :list="sQuestionDetails.codeOrderSlots"
      class="list-group"
      handle=".handle"
      item-key="order"
    >
      <template #item="{ element, index }">
        <div :key="index">
          <CodeOrderSlotEditor
            v-model:questionSlot="sQuestionDetails.codeOrderSlots[index]"
            :canDelete="sQuestionDetails.codeOrderSlots.length > 3"
            :language="sQuestionDetails.language"
            v-on:delete-row="removeRow(index)"
            v-on:add-order="addOrderQuestion(element)"
            v-on:remove-order="rmOrderQuestion(element)"
          />
        </div>
      </template>
    </draggable>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import CodeOrderSlotEditor from '@/components/code-order/CodeOrderSlotEditor.vue';
import draggable from 'vuedraggable';
import CodeOrderSlot from '@/models/management/questions/CodeOrderSlot';

const props = withDefaults(defineProps<{
  questionDetails: CodeOrderQuestionDetails;
  readonlyEdit?: boolean;
}>(), {
  readonlyEdit: true
});

const emit = defineEmits(['update:questionDetails']);

const sQuestionDetails = computed({
  get: () => props.questionDetails,
  set: (val) => emit('update:questionDetails', val),
});

const languages = computed(() => ['Java', 'Javascript', 'Python', 'CSharp']);

const updateList = () => {
  sQuestionDetails.value.codeOrderSlots = sQuestionDetails.value.codeOrderSlots.sort((a, b) => {
    if (a.order == null) return 1;
    if (b.order == null) return -1;
    return a.order > b.order ? 1 : -1;
  });
  endedReorder();
};

const endedReorder = () => {
  sQuestionDetails.value.codeOrderSlots.forEach((element: CodeOrderSlot, index: number) => {
    element.order = element.order != null ? index : element.order;
  });
};

const newSlot = () => {
  let newOrderSlot = new CodeOrderSlot();
  newOrderSlot.order = sQuestionDetails.value.codeOrderSlots.length;
  sQuestionDetails.value.codeOrderSlots.push(newOrderSlot);
  updateList();
};

onMounted(() => {
  while (sQuestionDetails.value.codeOrderSlots.length < 3) {
    newSlot();
  }
});

const addOrderQuestion = (element: CodeOrderSlot) => {
  element.order = sQuestionDetails.value.codeOrderSlots.length;
  updateList();
};

const rmOrderQuestion = (element: CodeOrderSlot) => {
  element.order = null;
  updateList();
};

const removeRow = (index: number) => {
  sQuestionDetails.value.codeOrderSlots.splice(index, 1);
  updateList();
};
</script>

<style lang="scss">
.code-order-create .CodeMirror {
  height: auto;
}
</style>
