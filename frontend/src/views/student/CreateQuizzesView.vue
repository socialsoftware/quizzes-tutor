<template>
  <v-container fill-height>
    <v-container class="create-buttons">
      <v-container>
        <p>Topic</p>
        <v-btn-toggle v-model="statementManager.assessment" mandatory>
          <v-btn
            v-for="assessment in availableAssessments"
            text
            :value="assessment.id"
            :key="assessment.id"
            >{{ assessment.title }}</v-btn
          >
          <v-btn text value="all">All</v-btn>
        </v-btn-toggle>
      </v-container>

      <v-container>
        <p class="pl-0">Questions</p>
        <v-btn-toggle v-model="statementManager.questionType" mandatory>
          <v-btn text value="failed">Failed</v-btn>
          <v-btn text value="new">New</v-btn>
          <v-btn text value="all">All</v-btn>
        </v-btn-toggle>
      </v-container>

      <!--      <v-layout row wrap align-center>
        <v-flex xs12>
          <p class="pl-0">Number of Questions</p>
          <v-btn-toggle v-model="statementManager.numberOfQuestions" mandatory>
            <v-btn text value="5">5</v-btn>
            <v-btn text value="10">10</v-btn>
            <v-btn text value="20">20</v-btn>
          </v-btn-toggle>
        </v-flex>
      </v-layout>-->
      <v-container>
        <v-btn @click="createQuiz" depressed color="primary">
          Create quiz
        </v-btn>
      </v-container>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import StatementManager from "@/models/statement/StatementManager";
import Assessment from "@/models/management/Assessment";
import RemoteServices from "@/services/RemoteServices";

@Component
export default class CreateQuizzesView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  availableAssessments: Assessment[] = [];

  // noinspection JSUnusedGlobalSymbols
  async beforeMount() {
    this.statementManager.reset();
    try {
      this.availableAssessments = await RemoteServices.getAvailableAssessments();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
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

<style lang="scss">
.create-buttons div {
  width: 50%;
  background-color: white;
}
</style>
