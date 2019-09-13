<template>
  <v-layout row class="list">
    <v-flex xs12 sm6 offset-sm3>
      <v-card>
        <v-list>
          <template v-for="(quiz, index) in quizzes">
            <v-divider v-if="index !== 0" :key="quiz.quizAnswerId"></v-divider>
            <v-list-tile :key="quiz.title">
              <v-list-tile-content>
                <v-list-tile-title v-html="quiz.title"></v-list-tile-title>
                <v-list-tile-title v-html="quiz.answerDate"></v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
          </template>
        </v-list>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import RemoteServices from "@/services/RemoteServices";
import SolvedQuiz from "@/models/statement/SolvedQuiz";

@Component
export default class AvailableQuizzesView extends Vue {
  quizzes: SolvedQuiz[] = [];

  async beforeMount() {
    try {
      this.quizzes = await RemoteServices.getSolvedQuizzes();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }
}
</script>

<style>
.list {
  margin-top: 50px;
}
</style>
