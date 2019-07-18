<template>
  <div class="question-container" v-if="question">
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
        v-html="convertMarkDown(question.content)"
      ></div>
      <div class="square" @click="increaseOrder">
        <i class="fas fa-chevron-right"></i>
      </div>
    </div>
    <ul class="option-list">
      <li
        v-bind:class="['option', answer.option === 0 ? 'selected' : '']"
        @click="selectOption(0)"
      >
        <span class="option-letter">A</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[0].content)"
        ></span>
      </li>
      <li
        v-bind:class="['option', answer.option === 1 ? 'selected' : '']"
        @click="selectOption(1)"
      >
        <span class="option-letter">B</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[1].content)"
        ></span>
      </li>
      <li
        v-bind:class="['option', answer.option === 2 ? 'selected' : '']"
        @click="selectOption(2)"
      >
        <span class="option-letter">C</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[2].content)"
        ></span>
      </li>
      <li
        v-bind:class="['option', answer.option === 3 ? 'selected' : '']"
        @click="selectOption(3)"
      >
        <span class="option-letter">D</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[3].content)"
        ></span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import Question from "@/models/Question";
import Answer from "@/models/Answer";
import showdown from "showdown";

@Component
export default class QuestionComponent extends Vue {
  @Model("order", Number) order: number | undefined;
  @Prop(Question) readonly question: Question | undefined;
  @Prop(Answer) answer: Answer | undefined;
  hover: boolean = false;

  constructor() {
    super();
  }

  convertMarkDown(text: string): string {
    const converter = new showdown.Converter();

    if (this.question && this.question.image) {
      text +=
        "  \n  \n  \n[image]: " +
        process.env.VUE_APP_ROOT_API +
        "/images/questions/" +
        this.question.image.url +
        ' "Image"';
    }

    let str = converter.makeHtml(text);
    //remove root paragraphs <p></p>
    str = str.substring(3);
    str = str.substring(0, str.length - 4);

    return str;
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
  selectOption(option: number) {
    if (this.answer && this.answer.option === option) {
      this.answer.option = null;
      return this.answer;
    } else if (this.answer) {
      this.answer.option = option;
      return this.answer;
    }
  }
}
</script>

<style lang="scss"></style>
