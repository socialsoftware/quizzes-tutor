<template>
  <v-container fluid v-if="availableAssessments.length > 0">
    <h2>Create Random Quiz</h2>
    <v-container class="create-buttons">
      <p>Assessment</p>
      <v-btn-toggle v-model="assessmentId" mandatory class="button-group">
        <v-btn
          v-for="assessment in availableAssessments"
          text
          :value="assessment.id"
          :key="assessment.id!"
          >{{ assessment.title }}</v-btn
        >
        <!--          <v-btn text value="all">All</v-btn>-->
      </v-btn-toggle>

      <div>
        <p class="pl-0">Number of Questions</p>
        <v-btn-toggle
          v-model="numberOfQuestions"
          mandatory
          class="button-group"
        >
          <v-btn text value="5">5</v-btn>
          <v-btn text value="10">10</v-btn>
          <v-btn text value="20">20</v-btn>
        </v-btn-toggle>
      </div>
      <div>
        <v-btn @click="createQuiz" depressed color="primary">
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useStore } from '@/store';
import { useRouter } from 'vue-router';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';

const store = useStore();
const router = useRouter();

const assessmentId = ref<number | null>(null);
const numberOfQuestions = ref<number | null>(null);
const availableAssessments = ref<Assessment[]>([]);

onMounted(async () => {
  store.setLoading();
  try {
    availableAssessments.value = await RemoteServices.getAvailableAssessments();
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
});

const createQuiz = async () => {
  store.setLoading();
  try {
    let statementQuiz: StatementQuiz =
      await RemoteServices.generateStatementQuiz({
        assessment: assessmentId.value,
        numberOfQuestions: numberOfQuestions.value,
      });
    store.statementQuiz = statementQuiz;
    router.push({
      name: 'solve-quiz',
    });
  } catch (error) {
    store.setError(error as string);
  }
  store.clearLoading();
};
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
