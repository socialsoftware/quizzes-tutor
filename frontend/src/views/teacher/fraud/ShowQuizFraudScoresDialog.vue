<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    styles="overflow:hidden"
  >
    <v-card v-if="quizFraudInformation">
      <v-card-title> Fraud Scores of {{ quiz.title }} </v-card-title>
      <v-alert dense type="warning" elevation="2">
        THIS IS A WIP FEATURE, DO NOT CONSIDER THE VALUES BELOW
      </v-alert>
      <v-container>
        <v-row
          ><v-col class="col-sm-12 col-md-4">
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
          <v-col class="col-sm-12 col-md-4"
            ><fraud-violin
              graphId="2"
              title="Consumer Scores"
              class="fraudViolin"
              :quizFraudScores="
                quizFraudScores.map((e) => ({
                  userInfo: e.userInfo,
                  score: e.scoreCommunicationConsumer,
                }))
              "
            ></fraud-violin
          ></v-col>
          <v-col class="col-sm-12 col-md-4"
            ><fraud-violin
              graphId="3"
              title="Producer Scores"
              class="fraudViolin"
              :quizFraudScores="
                quizFraudScores.map((e) => ({
                  userInfo: e.userInfo,
                  score: e.scoreCommunicationProducer,
                }))
              "
            ></fraud-violin></v-col
        ></v-row>
      </v-container>
      <v-data-table
        :headers="headers"
        :items="quizFraudScores"
        :sort-by="['scoreTime']"
        sort-desc
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      />
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import FraudViolin from '@/views/teacher/fraud/FraudViolin.vue';
import { QuizFraudInformation } from '@/models/management/fraud/QuizFraudInformation';
import { UserFraudScore } from '@/models/management/fraud/UserFraudScore';
import { Quiz } from '@/models/management/Quiz';

@Component({ components: { 'fraud-violin': FraudViolin } })
export default class ShowQuizFraudScoresDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  @Prop({ type: QuizFraudInformation, required: true })
  readonly quizFraudInformation!: QuizFraudInformation;
  @Prop({ type: Quiz, required: true })
  readonly quiz!: Quiz;

  headers: any[] = [
    { text: 'User', value: 'userInfo.name' },
    { text: 'Time Score', value: 'scoreTime' },
    { text: 'Consumer Score', value: 'scoreCommunicationConsumer' },
    { text: 'Production Score', value: 'scoreCommunicationProducer' },
  ];
  get quizFraudScores() {
    const result: UserFraudScore[] = [];
    for (let entry in this.quizFraudInformation.users) {
      result.push(this.quizFraudInformation.users[entry]);
    }
    return result;
  }
}
</script>
