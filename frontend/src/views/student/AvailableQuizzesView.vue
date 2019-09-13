<template>
  <v-layout row>
    <v-flex xs12 sm6 offset-sm3>
      <v-card>
        <v-list two-line>
          <template v-for="quiz in quizzes">
            <v-subheader v-if="quiz" :key="quiz.quizAnswerId">
              {{ quiz.title }}
            </v-subheader>
            <v-list-tile :key="quiz.title">
              <v-list-tile-content>
                <v-list-tile-title v-html="quiz.title"></v-list-tile-title>
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
import StatementQuiz from "@/models/statement/StatementQuiz";

@Component
export default class AvailableQuizzesView extends Vue {
  quizzes: StatementQuiz[] = [];

  async beforeMount() {
    try {
      this.quizzes = await RemoteServices.getAvailableQuizzes();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
  }
}
</script>

<style></style>
