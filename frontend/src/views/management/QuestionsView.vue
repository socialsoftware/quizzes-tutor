<template>
  <div v-if="questions">
    <div v-for="question in questions" :key="question.id">
      <p v-html="convertMarkDown(question.content, question.image)"></p>
      <ul>
        <li
          v-for="option in question.options"
          :key="option.id"
          v-html="convertMarkDown(option.content)"
        ></li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import { Question, QuestionDto } from "@/models/question/Question";
import RemoteServices from "@/services/RemoteServices";
import { convertMarkDown } from "@/scripts/script";
import Image from "@/models/Image";

@Component
export default class QuestionsMangement extends Vue {
  questions: Question[] | null = null;
  constructor() {
    super();
  }

  beforeMount() {
    RemoteServices.getQuestions().then(result => {
      this.questions = result.data.map(
        (question: QuestionDto) => new Question(question)
      );
    });
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss"></style>
