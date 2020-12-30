<template>
  <div class="code-order-create-slot">
    <i class="fa fa-align-justify handle"></i>
    <div>{{ sQuestionSlot.order }}</div>
    <v-textarea
      class="slot-content"
      v-model="sQuestionSlot.content"
      :rules="[() => !!sQuestionSlot.content || 'This field is required']"
      label="Slot Content"
      auto-grow
      required
      data-cy="questionQuestionTextArea"
      rows="4"
    ></v-textarea>
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

<script lang="ts">
import CodeOrderSlot from '@/models/management/questions/CodeOrderSlot';
import { Component, PropSync, Vue, Prop } from 'vue-property-decorator';

@Component
export default class CodeOrderSlotEditor extends Vue {
  @PropSync('questionSlot', { type: CodeOrderSlot })
  sQuestionSlot!: CodeOrderSlot;
  @Prop({ default: false })
  readonly canDelete!: boolean;
}
</script>

<style lang="scss">
.code-order-create-slot {
  background-color: white;
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  & > * {
    min-width: 30px;
    margin: auto;
  }

  & > .slot-content {
    flex-grow: 1;
  }

  & > .toolbar {
    display: inline-flex;
    flex-direction: column;
    justify-content: center;
  }
}
</style>
