<template>
  <div class="container">
    <h2>Solved Quizzes</h2>
    <ul data-cy="solvedQuizzesList">
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Solved Date</div>
        <div class="col">Score</div>
        <div class="col last-col"></div>
      </li>
      <li
        class="list-row"
        v-for="quiz in quizzes"
        :key="quiz.statementQuiz.id + quiz.answerDate"
        @click="showResults(quiz)"
      >
        <div class="col">
          {{ quiz.statementQuiz.title }}
        </div>
        <div class="col">
          {{ quiz.answerDate }}
        </div>
        <div class="col">
          {{ calculateScore(quiz) }}
        </div>
        <div class="col last-col">
          <i class="fas fa-chevron-circle-right" />
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
import SolvedQuiz from '@/models/statement/SolvedQuiz';

const store = useStore();
const router = useRouter();

const quizzes = ref<SolvedQuiz[]>([]);

onMounted(async () => {
  store.setLoading();
  try {
    quizzes.value = (await RemoteServices.getSolvedQuizzes()).reverse();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const calculateScore = (quiz: SolvedQuiz) => {
  let correct = 0;
  for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
    if (
      quiz.statementQuiz.answers[i] &&
      quiz.statementQuiz.answers[i].isAnswerCorrect(quiz.correctAnswers[i])
    ) {
      correct += 1;
    }
  }
  return `${correct}/${quiz.statementQuiz.questions.length}`;
};

const showResults = async (quiz: SolvedQuiz) => {
  store.setStatementQuiz(quiz.statementQuiz);
  store.setCorrectAnswers(quiz.correctAnswers);
  await router.push({ name: 'quiz-results' });
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
    small {
      font-size: 0.5em;
    }
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
      width: 25%;
    }

    .last-col {
      max-width: 50px !important;
    }

    .list-row {
      background-color: #ffffff;
      cursor: pointer;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
    }

    .list-row:hover {
      background-color: #c8c8c8;
    }
  }
}
</style>
