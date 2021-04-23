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
        :disabled="disabled"
      >
        <div class="col">
          {{ quiz.title }}
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

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';
import StatementQuiz from '@/models/statement/StatementQuiz';

@Component
export default class AvailableQuizzesView extends Vue {
  quizzes: StatementQuiz[] = [];
  disabled: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.quizzes = (await RemoteServices.getAvailableQuizzes()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async solveQuiz(quiz: StatementQuiz) {
    if (!this.disabled) {
      // handle double clicks
      this.disabled = true;

      let statementManager: StatementManager = StatementManager.getInstance;

      try {
        statementManager.statementQuiz = await RemoteServices.startQuiz(
          quiz.id
        );
        await this.$router.push({ name: 'solve-quiz' });
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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
