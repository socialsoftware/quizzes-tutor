<template>
  <v-container fill-height>
    <v-container grid-list-md text-xs-center>
      <v-layout row wrap align-center>
        <v-flex xs12>
          <p>Topic</p>
          <v-btn-toggle v-model="topic" mandatory>
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
          <v-btn-toggle v-model="questions" mandatory>
            <v-btn flat value="failed">Failed</v-btn>
            <v-btn flat value="new">New</v-btn>
            <v-btn flat value="all">All</v-btn>
          </v-btn-toggle>
        </v-flex>
      </v-layout>

      <v-layout row wrap align-center>
        <v-flex xs12>
          <p class="pl-0">Number of Questions</p>
          <v-btn-toggle v-model="numberOfQuestions" mandatory>
            <v-btn flat value="5">5</v-btn>
            <v-btn flat value="10">10</v-btn>
            <v-btn flat value="20">20</v-btn>
          </v-btn-toggle>
        </v-flex>
      </v-layout>

      <v-btn @click="createQuiz" depressed color="primary">Create quiz</v-btn>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Quiz from "@/models/Quiz";

@Component
export default class SetupView extends Vue {
  topic: string = "1";
  questions: string = "new";
  numberOfQuestions: string = "5";

  async createQuiz() {
    await Quiz.getQuiz(
      this.topic,
      this.questions,
      parseInt(this.numberOfQuestions)
    );
    this.$router.push("/quiz");
  }
}
</script>

<style></style>
