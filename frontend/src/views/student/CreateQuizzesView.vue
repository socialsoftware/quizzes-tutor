<template>
  <v-container fluid v-if="availableAssessments.length > 0">
    <h2>Create Random Quiz</h2>
    <v-container class="create-buttons">
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
        <!--          <v-btn text value="all">All</v-btn>-->
      </v-btn-toggle>

      <div>
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
      </div>
      <div>
        <v-btn
          @click="createQuiz"
          depressed
          :disabled="disabled"
          color="primary"
        >
          Create quiz
        </v-btn>
      </div>
    </v-container>
  </v-container>
  <v-container fluid v-else>
    <h2>No assessment available</h2>
    <v-container class="create-buttons">
      <p>Ask your teacher to create an assessment</p>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CreateQuizzesView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  availableAssessments: Assessment[] = [];
  disabled: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    this.statementManager.reset();
    try {
      this.availableAssessments = await RemoteServices.getAvailableAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async createQuiz() {
    this.disabled = true;
    try {
      await this.statementManager.getQuizStatement();
      await this.$router.push({ name: 'solve-quiz' });
    } catch (error) {
      await this.$store.dispatch('error', error);
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
  padding: 20px;
  flex-wrap: wrap;
  justify-content: center;
}
</style>
