<template>
  <div class="container">
    <h2>Create Random Quiz</h2>
    <v-container class="create-buttons">
      <v-container>
        <p>Assessment</p>
        <v-btn-toggle
          v-model="statementManager.assessment"
          mandatory
          class="button-group"
        >
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

      <!--      <v-container>
        <p class="pl-0">Questions</p>
        <v-btn-toggle
          v-model="statementManager.questionType"
          mandatory
          class="button-group"
        >
          <v-btn text value="failed">Only Failed</v-btn>
          <v-btn text value="new">Only New</v-btn>
          <v-btn text value="all">All</v-btn>
        </v-btn-toggle>
      </v-container>-->

      <v-container>
        <p class="pl-0">Number of Questions</p>
        <v-btn-toggle
          v-model="statementManager.numberOfQuestions"
          mandatory
          class="button-group"
        >
          <v-btn text value="5">5</v-btn>
          <v-btn text value="10">10</v-btn>
          <v-btn text value="20">20</v-btn>
        </v-btn-toggle>
      </v-container>
      <v-container>
        <v-btn @click="createQuiz" depressed color="primary">
          Create quiz
        </v-btn>
      </v-container>
    </v-container>
  </div>
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

  async created() {
    await this.$store.dispatch("loading");
    this.statementManager.reset();
    try {
      this.availableAssessments = await RemoteServices.getAvailableAssessments();
    } catch (error) {
      await this.$store.dispatch("error", error);
    }
    await this.$store.dispatch("clearLoading");
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

<style lang="scss" scoped>
.create-buttons {
  width: 80% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>
