<template>
  <v-dialog v-model="dialog" @keydown.esc="closeQuizDialog" max-width="75%">
    <v-card v-if="quiz">
      <v-card-title>{{ quiz.title }}</v-card-title>

      <v-card-text>
        <question-list :questions="quiz.questions" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeQuizDialog">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import { Quiz } from "@/models/management/Quiz";
import QuestionsList from "@/views/teacher/questions/QuestionsList.vue";

@Component({
  components: {
    "question-list": QuestionsList
  }
})
export default class ShowQuizDialog extends Vue {
  @Prop({ type: Quiz, required: true }) readonly quiz!: Quiz | null;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeQuizDialog() {
    this.$emit("close-quiz-dialog");
  }
}
</script>
