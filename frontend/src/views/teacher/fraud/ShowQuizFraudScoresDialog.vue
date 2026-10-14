<template>
  <v-dialog
    v-model="dialogOpen"
    @keydown.esc="dialogOpen = false"
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
      <v-alert border="start" colored-border type="info" elevation="2" class="text-left">
        <b>Speed</b> flags students whose answers came faster than the question and its options can be
        read, or at almost the same time whatever their length (the time is the one the server
        recorded). It is a sign to look at, <b>never proof</b>: a fast reader or a question seen before
        can look like this too. Screenshots cannot be detected.
      </v-alert>
      <v-data-table
        :headers="headers"
        :items="quizFraudScores"
        :sort-by="[{ key: 'scoreTime', order: 'desc' }]"
        :mobile-breakpoint="0"
        :items-per-page="15"
        :items-per-page-options="[15, 30, 50, 100]"
      >
        <template v-slot:[`item.speedPoints`]="{ item }">
          <v-chip
            v-if="item.speedLevel && item.speedLevel !== 'NONE'"
            :color="levelColor(item.speedLevel)"
            size="small"
            data-cy="SpeedLevel"
            >{{ item.speedLevel }}</v-chip
          >
          <span v-else-if="item.speedLevel" class="text-medium-emphasis">-</span>
        </template>
        <template v-slot:[`item.speedReasons`]="{ item }">
          <ul v-if="item.speedReasons && item.speedReasons.length" class="text-left" data-cy="SpeedReasons">
            <li v-for="reason in item.speedReasons" :key="reason">{{ reason }}</li>
          </ul>
        </template>
      </v-data-table>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import FraudViolin from '@/views/teacher/fraud/FraudViolin.vue';
import { QuizFraudInformation } from '@/models/management/fraud/QuizFraudInformation';
import { UserFraudScore } from '@/models/management/fraud/UserFraudScore';
import { Quiz } from '@/models/management/Quiz';

const dialogOpen = defineModel<boolean>('dialog', { default: false });

const props = defineProps<{
  quizFraudInformation: QuizFraudInformation;
  quiz: Quiz;
}>();

const headers: any[] = [
  { title: 'User', key: 'userInfo.name' },
  { title: 'Time Score', key: 'scoreTime' },
  { title: 'Consumption Score', key: 'scoreCommunicationConsumer' },
  { title: 'Production Score', key: 'scoreCommunicationProducer' },
  { title: 'Speed', key: 'speedPoints' },
  { title: 'Why', key: 'speedReasons', sortable: false },
];

const levelColor = (level: string) => (level === 'HIGH' ? 'error' : level === 'MEDIUM' ? 'warning' : 'info');

const quizFraudScores = computed(() => {
  const result: UserFraudScore[] = [];
  for (let entry in props.quizFraudInformation.users) {
    result.push(props.quizFraudInformation.users[entry]);
  }
  return result;
});
</script>
