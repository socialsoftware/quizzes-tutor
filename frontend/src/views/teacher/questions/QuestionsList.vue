<template>
  <v-container grid-list-md fluid>
    <v-layout column wrap>
      <ol>
        <li
          v-for="question in questions"
          :key="question.sequence"
          class="text-left"
        >
          <QuestionView :question="question" />
        </li>
      </ol>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from "vue-property-decorator";
import Question from "../../../models/management/Question";
import QuestionView from "@/views/teacher/questions/QuestionView.vue";

@Component({
  components: { QuestionView }
})
export default class QuestionsList extends Vue {
  @Prop({ type: Array, required: true }) readonly questions!: Question[];
}
</script>

<style scoped></style>
