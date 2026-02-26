<template>
  <div>
    <quiz-form
      v-if="editMode && quiz"
      @switchMode="changeMode"
      @updateQuiz="updateQuiz"
      :edit-mode="editMode"
      :quiz="quiz"
    />
    <quiz-list
      v-if="!editMode"
      @editQuiz="editQuiz"
      @updateQuiz="updateQuiz"
      @duplicateQuiz="duplicateQuiz"
      @deleteQuiz="deleteQuiz"
      @newQuiz="newQuiz"
      :quizzes="quizzes"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import RemoteServices from '@/services/RemoteServices';
import { Quiz } from '@/models/management/Quiz';
import QuizForm from '@/views/teacher/quizzes/QuizForm.vue';
import QuizList from '@/views/teacher/quizzes/QuizList.vue';

const store = useStore();
const quizzes = ref<Quiz[]>([]);
const quiz = ref<Quiz | null>(null);
const editMode = ref(false);

onMounted(async () => {
  store.setLoading();
  try {
    quizzes.value = await RemoteServices.getNonGeneratedQuizzes();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const changeMode = () => {
  editMode.value = !editMode.value;
  if (editMode.value) {
    quiz.value = new Quiz();
  } else {
    quiz.value = null;
  }
};

const editQuiz = async (quizId: number) => {
  try {
    quiz.value = await RemoteServices.getQuiz(quizId);
    editMode.value = true;
  } catch (error) {
    store.setError(error as string);
  }
};

const updateQuiz = (updatedQuiz: Quiz) => {
  quizzes.value = quizzes.value.filter((q) => q.id !== updatedQuiz.id);
  quizzes.value.unshift(updatedQuiz);
  editMode.value = false;
  quiz.value = null;
};

const duplicateQuiz = (duplicatedQuiz: Quiz) => {
  quizzes.value.unshift(duplicatedQuiz);
};

const deleteQuiz = (quizId: number) => {
  quizzes.value = quizzes.value.filter((q) => q.id !== quizId);
};

const newQuiz = () => {
  editMode.value = true;
  quiz.value = new Quiz();
};
</script>

<style lang="scss" scoped />
