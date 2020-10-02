<template>
  <div class="container">
    <v-card v-if="!hasCode" class="justify-center">
      <v-card-text class="justify-center">
        <v-form ref="form" v-if="!hasCode">
          <v-row justify="center">
            <v-col cols="8" sm="4" md="2">
              <v-text-field
                label="Code"
                v-model="quizId"
                outlined
              ></v-text-field>
            </v-col>
          </v-row>
          <v-btn color="green darken-1" @click="setQuizId">Save</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
    <v-card v-else>
      <v-card-title class="justify-center">
        Hold on and wait {{ timer }} to start the quiz!
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QrcodeStream } from 'vue-qrcode-reader';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component
export default class CodeView extends Vue {
  hasCode: boolean = false;
  quizId: number | null = null;
  quiz: StatementQuiz | null = null;
  timer: string = '';

  async setQuizId() {
    this.hasCode = true;
    this.getQuizByCode();
  }

  async getQuizByCode() {
    if (this.quizId && this.$router.currentRoute.name === 'code') {
      try {
        this.quiz = await RemoteServices.getQuizByQRCode(this.quizId);

        if (!this.quiz.timeToAvailability) {
          let statementManager: StatementManager = StatementManager.getInstance;
          statementManager.statementQuiz = this.quiz;
          await this.$router.push({ name: 'solve-quiz' });
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
  }

  @Watch('quiz.timeToAvailability')
  timerMethod() {
    if (!!this.quiz && !this.quiz.timeToAvailability) {
      this.getQuizByCode();
    }

    this.timer = milisecondsToHHMMSS(this.quiz?.timeToAvailability);
  }
}
</script>

<style lang="scss" scoped></style>
