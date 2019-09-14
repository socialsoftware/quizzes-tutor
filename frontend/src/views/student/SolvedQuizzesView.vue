<template>
  <div class="container">
    <h2>Solved Quizzes</h2>
    <ul class="responsive-table">
      <li class="table-header">
        <div class="col">Title</div>
        <div class="col">Solved Date</div>
        <div class="col">Score</div>
        <div class="col last-col"></div>
      </li>
    </ul>
    <ul class="responsive-table scrollbar">
      <li class="table-row" v-for="quiz in quizzes" :key="quiz.quizAnswerId">
        <div class="col" data-label="title">
          {{ quiz.title }}
        </div>
        <div class="col" data-label="solved-date">
          {{ quiz.answerDate }}
        </div>
        <div class="col" data-label="score">
          {{ calculateScore(quiz) }}
        </div>
        <div class="col last-col" data-label="button">
          <i class="fas fa-chevron-circle-right" @click="showResults(quiz)"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import SolvedQuiz from "@/models/statement/SolvedQuiz";
import StatementManager from "@/models/statement/StatementManager";

@Component
export default class AvailableQuizzesView extends Vue {
  quizzes: SolvedQuiz[] = [];

  async beforeMount() {
    try {
      this.quizzes = (await RemoteServices.getSolvedQuizzes()).reverse();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }

  calculateScore(quiz: SolvedQuiz) {
    let correct = 0;
    for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
      if (quiz.correctAnswers[i].correctOptionId === quiz.answers[i].optionId) {
        correct += 1;
      }
    }
    return `${correct}/${quiz.statementQuiz.questions.length}`;
  }

  async showResults(quiz: SolvedQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.answers = quiz.answers;
    statementManager.correctAnswers = quiz.correctAnswers;
    statementManager.statementQuiz = quiz.statementQuiz;
    await this.$router.push({ name: "quiz-results" });
  }
}
</script>

<style lang="scss">
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

  .responsive-table {
    padding: 0 5px;
    max-height: 70vh;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .table-header {
      background-color: #95a5a6;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .last-col {
      max-width: 50px !important;
    }

    .table-row {
      background-color: #ffffff;
      box-shadow: 0px 0px 9px 0px rgba(0, 0, 0, 0.1);
    }

    @media all and (max-width: 767px) {
      .table-header {
        display: none;
      }
      .table-row {
      }
      li {
        display: block;
      }
      .col {
        flex-basis: 100%;
      }
      .col {
        display: flex;
        padding: 10px 0;
        &:before {
          color: #6c7a89;
          padding-right: 10px;
          content: attr(data-label);
          flex-basis: 50%;
          text-align: right;
        }
      }
    }
  }
}
</style>
