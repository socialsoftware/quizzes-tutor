<template>
  <div class="container">
    <h2>Available Quizzes</h2>
    <ul data-cy="availableQuizzesList">
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Available since</div>
        <div class="col">Available until</div>
        <div class="col last-col"></div>
      </li>
      <li
        class="list-row"
        v-for="quiz in quizzes"
        :key="quiz.quizAnswerId"
        @click="solveQuiz(quiz)"
      >
        <div class="col">
          {{ quiz.title }}
          <v-chip v-if="quiz.singleSession" size="x-small" color="warning" class="ml-1" data-cy="SingleSessionBadge">Opens only once</v-chip>
          <v-chip v-if="quiz.safeBrowserOnly" size="x-small" color="info" class="ml-1" data-cy="SafeBrowserBadge">Safe Exam Browser only</v-chip>
        </div>
        <div class="col">
          {{ quiz.availableDate }}
        </div>
        <div class="col">
          {{ quiz.conclusionDate }}
        </div>
        <div class="col last-col">
          <i class="fas fa-chevron-circle-right"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';

const store = useStore();
const router = useRouter();

const quizzes = ref<StatementQuiz[]>([]);

onMounted(async () => {
  store.setLoading();
  try {
    quizzes.value = (await RemoteServices.getAvailableQuizzes()).reverse();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const solveQuiz = async (quiz: StatementQuiz) => {
  // A quiz that opens once is lost if the page is closed or reloaded: the student is told before it starts
  if (
    quiz.singleSession &&
    !confirm(
      `"${quiz.title}" can only be opened once. If you close or reload the page you will not be able to come back to it. Open it now?`
    )
  )
    return;
  store.setLoading();
  try {
    let statementQuiz: StatementQuiz = await RemoteServices.startQuiz(quiz.id);
    store.setStatementQuiz(statementQuiz);
    await router.push({ name: 'solve-quiz' });
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
