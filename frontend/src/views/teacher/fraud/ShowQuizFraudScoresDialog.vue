<template>
  <v-dialog
    :model-value="dialog"
    @update:model-value="$emit('update:dialog', false)"
    @keydown.esc="$emit('update:dialog', false)"
    max-width="75%"
    styles="overflow:hidden"
  >
    <v-card v-if="quizFraudInformation">
      <v-card-title> Fraud Scores of {{ quiz.title }} </v-card-title>
      <v-alert border="start" colored-border type="warning" elevation="2">
        <v-row>
          <v-col class="col-sm-21 col-md-7 grow text-left font-weight-medium">
            Time Scores should be interpreted according to Pedro Caldeira MSc
            Thesis
          </v-col>
          <v-col class="shrink col-sm-6 col-md-2">
            <v-btn
              icon
              href="https://fenix.tecnico.ulisboa.pt/cursos/meic-a/dissertacao/1409728525633167"
              target="_blank"
              >ACCESS THESIS</v-btn
            >
          </v-col>
        </v-row>
      </v-alert>
      <v-alert border="start" colored-border type="warning" elevation="2">
        <v-row>
          <v-col class="col-sm-21 col-md-7 grow text-left font-weight-medium">
            Production and Consumption scores should be interpreted according to
            Mariana Carrasco MSc Thesis
          </v-col>
          <v-col class="shrink col-sm-6 col-md-2">
            <v-btn
              icon
              href="https://fenix.tecnico.ulisboa.pt/cursos/mecd/dissertacao/1128253548922811"
              target="_blank"
              >ACCESS THESIS</v-btn
            >
          </v-col>
        </v-row>
      </v-alert>
      <v-container>
        <v-row>
          <v-col class="col-sm-12 col-md-4">
            <fraud-violin
              graphId="1"
              class="fraudViolin"
              title="Time Scores"
              :quizFraudScores="
                quizFraudScores.map((e) => ({
                  userInfo: e.userInfo,
                  score: e.scoreTime,
                }))
              "
            ></fraud-violin>
          </v-col>
          <v-col class="col-sm-12 col-md-4">
            <fraud-violin
              graphId="2"
              title="Consumption Scores"
              class="fraudViolin"
              :quizFraudScores="
                quizFraudScores.map((e) => ({
                  userInfo: e.userInfo,
                  score: e.scoreCommunicationConsumer,
                }))
              "
            ></fraud-violin>
          </v-col>
          <v-col class="col-sm-12 col-md-4">
            <fraud-violin
              graphId="3"
              title="Production Scores"
              class="fraudViolin"
              :quizFraudScores="
                quizFraudScores.map((e) => ({
                  userInfo: e.userInfo,
                  score: e.scoreCommunicationProducer,
                }))
              "
            ></fraud-violin>
          </v-col>
        </v-row>
      </v-container>
      <v-data-table
        :headers="headers"
        :items="quizFraudScores"
        :sort-by="[{ key: 'scoreTime', order: 'desc' }]"
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      />
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import FraudViolin from '@/views/teacher/fraud/FraudViolin.vue';
import { QuizFraudInformation } from '@/models/management/fraud/QuizFraudInformation';
import { UserFraudScore } from '@/models/management/fraud/UserFraudScore';
import { Quiz } from '@/models/management/Quiz';

const props = defineProps<{
  dialog: boolean;
  quizFraudInformation: QuizFraudInformation;
  quiz: Quiz;
}>();

defineEmits(['update:dialog']);

const headers: any[] = [
  { title: 'User', value: 'userInfo.name' },
  { title: 'Time Score', value: 'scoreTime' },
  { title: 'Consumption Score', value: 'scoreCommunicationConsumer' },
  { title: 'Production Score', value: 'scoreCommunicationProducer' },
];

const quizFraudScores = computed(() => {
  const result: UserFraudScore[] = [];
  for (let entry in props.quizFraudInformation.users) {
    result.push(props.quizFraudInformation.users[entry]);
  }
  return result;
});
</script>
