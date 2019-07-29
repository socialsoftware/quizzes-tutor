<template>
  <div v-if="questions">
    <div v-for="question in questions" :key="question.id">
      XXXX
      <!-- <p v-html="convertMarkDown(question.content, question.image)"/> -->
      <ul>
        <!-- <li 
          v-for="option in question.options" :key="option.id"
          v-html="convertMarkDown(option.content)"
        /> -->
      </ul>

    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from "vue-property-decorator";
import { Question, QuestionDto } from "@/models/question/Question";
import RemoteServices from "@/services/RemoteServices"
import { convertMarkDown } from "@/scripts/script"

@Component
export default class QuestionsMangement extends Vue {
  questions: Question[] | null = null;
  constructor() {
    super();
  }

  beforeMount() {
    RemoteServices.getQuestions()
    .then(result => {
      this.questions = result.data.map((question) => new Question(question));
    });
  }
  
}
</script>

<style lang="scss"></style>
