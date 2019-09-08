<template>
  <v-content>
    <quiz-form 
      @switchMode="changeMode" 
      @updateQuiz="updateQuiz" 
      :edit-mode="editMode"
      :quiz="quiz"></quiz-form>
    <quiz-list v-if="!editMode" 
      @deleteQuiz="deleteQuiz"
      :quizzes="quizzes"></quiz-list>
   </v-content>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import { Quiz } from "@/models/management/Quiz";
import QuizForm from "@/views/management/quizzes/QuizForm.vue";
import QuizList from "@/views/management/quizzes/QuizList.vue";

@Component({
  components: {
    QuizForm, 
    QuizList,
  }
})
export default class QuizzesView extends Vue {
    quizzes: Quiz[] = [];
    quiz: Quiz = new Quiz();
    editMode: boolean = false;

  constructor() {
    super();
  }

  // noinspection JSUnusedGlobalSymbols
  async beforeMount() {
    try {
      this.quizzes = await RemoteServices.getNonGeneratedQuizzes();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  changeMode() {
    this.editMode = !this.editMode;
  }

  updateQuiz(updatedQuiz: Quiz) {
    this.quizzes = this.quizzes.filter(quiz => quiz.id !== updatedQuiz.id);
    this.quizzes.push(updatedQuiz);
    this.editMode = false;
  }

  deleteQuiz(quizId: number) {
    this.quizzes = this.quizzes.filter(quiz => quiz.id !== quizId);
  }

}
</script>

<style lang="scss"></style>
