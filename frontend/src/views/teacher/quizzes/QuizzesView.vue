<template>
  <div>
    <quiz-form
      @switchMode="changeMode"
      @updateQuiz="updateQuiz"
      :edit-mode="editMode"
      :quiz="quiz"
    />
    <quiz-list
      v-if="!editMode"
      @editQuiz="editQuiz"
      @updateQuiz="updateQuiz"
      @deleteQuiz="deleteQuiz"
      @newQuiz="newQuiz"
      :quizzes="quizzes"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import QuizForm from '@/views/teacher/quizzes/QuizForm.vue';
import QuizList from '@/views/teacher/quizzes/QuizList.vue';

@Component({
  components: {
    QuizForm,
    QuizList,
  },
})
export default class QuizzesView extends Vue {
  quizzes: Quiz[] = [];
  quiz: Quiz | null = null;
  editMode: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.quizzes = await RemoteServices.getNonGeneratedQuizzes();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  changeMode() {
    this.editMode = !this.editMode;
    if (this.editMode) {
      this.quiz = new Quiz();
    } else {
      this.quiz = null;
    }
  }

  async editQuiz(quizId: number) {
    try {
      this.quiz = await RemoteServices.getQuiz(quizId);
      this.editMode = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  updateQuiz(updatedQuiz: Quiz) {
    this.quizzes = this.quizzes.filter((quiz) => quiz.id !== updatedQuiz.id);
    this.quizzes.unshift(updatedQuiz);
    this.editMode = false;
    this.quiz = null;
  }

  deleteQuiz(quizId: number) {
    this.quizzes = this.quizzes.filter((quiz) => quiz.id !== quizId);
  }

  newQuiz() {
    this.editMode = true;
    this.quiz = new Quiz();
  }
}
</script>

<style lang="scss" scoped />
