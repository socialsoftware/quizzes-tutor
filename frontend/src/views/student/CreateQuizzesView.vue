<template>
  <v-container fill-height>
    <v-container grid-list-md text-xs-center>
      <v-layout row wrap align-center>
        <v-flex xs12>
          <p>Topic</p>
          <v-btn-toggle v-model="statementManager.topic" multiple mandatory>
            <v-btn flat value="1">1</v-btn>
            <v-btn flat value="2">2</v-btn>
            <v-btn flat value="3">3</v-btn>
            <v-btn flat value="4">4</v-btn>
            <v-btn flat value="all">All</v-btn>
          </v-btn-toggle>
        </v-flex>
      </v-layout>

      <v-layout row wrap align-center>
        <v-flex xs12>
          <p class="pl-0">Questions</p>
          <v-btn-toggle v-model="statementManager.questionType" mandatory>
            <v-btn flat value="failed">Failed</v-btn>
            <v-btn flat value="new">New</v-btn>
            <v-btn flat value="all">All</v-btn>
          </v-btn-toggle>
        </v-flex>
      </v-layout>

      <v-layout row wrap align-center>
        <v-flex xs12>
          <p class="pl-0">Number of Questions</p>
          <v-btn-toggle v-model="statementManager.numberOfQuestions" mandatory>
            <v-btn flat value="5">5</v-btn>
            <v-btn flat value="10">10</v-btn>
            <v-btn flat value="20">20</v-btn>
          </v-btn-toggle>
        </v-flex>
      </v-layout>

      <v-btn @click="createQuiz" depressed color="primary">
        Create quiz
      </v-btn>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import StatementManager from "@/models/statement/StatementManager";

@Component
export default class CreateQuizzesView extends Vue {
  private statementManager: StatementManager = StatementManager.getInstance;

  // noinspection JSUnusedGlobalSymbols
  beforeMount() {
    this.statementManager.reset();
  }

  async createQuiz() {
    try {
      await this.statementManager.getQuizStatement();
      await this.$router.push({ name: "solve-quiz" });
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }
}
</script>

<style></style>
