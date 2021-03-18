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
        <template v-slot:activator="{ on }">
          <v-btn color="primary" small @click="newSlot" v-on="on"
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
    >
      <div
        v-for="(element, index) in sQuestionDetails.codeOrderSlots"
        :key="index"
      >
        <CodeOrderSlotEditor
          :questionSlot.sync="sQuestionDetails.codeOrderSlots[index]"
          :canDelete="sQuestionDetails.codeOrderSlots.length > 3"
          :language="sQuestionDetails.language"
          v-on:delete-row="removeRow(index)"
          v-on:add-order="addOrderQuestion(element)"
          v-on:remove-order="rmOrderQuestion(element)"
        />
      </div>
    </draggable>
  </div>
</template>

<script lang="ts">
import CodeOrderQuestionDetails from '@/models/management/questions/CodeOrderQuestionDetails';
import { Component, Vue, Prop, Watch, PropSync } from 'vue-property-decorator';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import CodeOrderSlotEditor from '@/components/code-order/CodeOrderSlotEditor.vue';
import draggable from 'vuedraggable';
import CodeOrderSlot from '@/models/management/questions/CodeOrderSlot';

@Component({
  components: {
    CodeOrderSlotEditor,
    draggable,
  },
})
export default class CodeOrderCreate extends Vue {
  @PropSync('questionDetails', { type: CodeOrderQuestionDetails })
  sQuestionDetails!: CodeOrderQuestionDetails;
  @Prop({ default: true }) readonly readonlyEdit!: boolean;

  get languages(): String[] {
    return BaseCodeEditor.availableLanguages;
  }

  mounted() {
    // minimum slots should be 3
    while (this.sQuestionDetails.codeOrderSlots.length < 3) {
      this.newSlot();
    }
  }

  newSlot() {
    let newOrderSlot = new CodeOrderSlot();
    newOrderSlot.order = this.sQuestionDetails.codeOrderSlots.length;
    this.sQuestionDetails.codeOrderSlots.push(newOrderSlot);
    this.updateList();
  }

  updateList() {
    this.sQuestionDetails.codeOrderSlots = this.sQuestionDetails.codeOrderSlots.sort(
      (a, b) => {
        if (a.order == null) {
          return 1;
        }
        if (b.order == null) {
          return -1;
        }
        return a.order > b.order ? 1 : -1;
      }
    );
    this.endedReorder();
  }

  addOrderQuestion(element: CodeOrderSlot) {
    element.order = this.sQuestionDetails.codeOrderSlots.length;
    this.updateList();
  }

  rmOrderQuestion(element: CodeOrderSlot) {
    element.order = null;
    this.updateList();
  }

  removeRow(index: number) {
    this.sQuestionDetails.codeOrderSlots.splice(index, 1);
    this.updateList();
  }

  endedReorder() {
    this.sQuestionDetails.codeOrderSlots.forEach(
      (element: CodeOrderSlot, index: number) => {
        element.order = element.order != null ? index : element.order;
      }
    );
  }
}
</script>

<style lang="scss">
.code-order-create .CodeMirror {
  height: auto;
}
</style>
