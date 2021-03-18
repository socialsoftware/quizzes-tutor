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
        :key="quiz.quizAnswerId"
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

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import StatementManager from '@/models/statement/StatementManager';

@Component
export default class SolvedQuizzesView extends Vue {
  quizzes: SolvedQuiz[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.quizzes = (await RemoteServices.getSolvedQuizzes()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateScore(quiz: SolvedQuiz) {
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
  }

  async showResults(quiz: SolvedQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.correctAnswers = quiz.correctAnswers;
    statementManager.statementQuiz = quiz.statementQuiz;
    await this.$router.push({ name: 'quiz-results' });
  }
}
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
