<template>
  <div class="question-container" v-if="question && question.options">
    <div class="question">
      <span
        class="square"
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
      >
        <i v-if="hover" class="fas fa-chevron-left"></i>
        <span v-else>{{ order + 1 }}</span></span
      >
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div class="square" @click="increaseOrder">
        <i class="fas fa-chevron-right"></i>
      </div>
    </div>
    <ul class="option-list">
      <li
        v-for="(n, index) in question.options.length"
        v-if="question.options[index]"
        :key="index"
        v-bind:class="[
          'option',
          optionId === question.options[index].optionId ? 'selected' : ''
        ]"
        @click="selectOption(question.options[index].optionId)"
      >
        <span class="option-letter">{{ optionLetters[index] }}</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[index].content)"
        ></span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import Question from "@/models/Question";
import { convertMarkDown } from "@/scripts/script";
import Image from "@/models/Image";

@Component
export default class QuestionComponent extends Vue {
  @Model("order", Number) order: number | undefined;
  @Prop(Question) readonly question: Question | undefined;
  @Prop(Number) optionId: number | undefined;
  hover: boolean = false;
  optionLetters: string[] = ["A", "B", "C", "D"];

  constructor() {
    super();
  }

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  @Emit()
  selectOption(optionId: number) {
    return optionId;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss"></style>
