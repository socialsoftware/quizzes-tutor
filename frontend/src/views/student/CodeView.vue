<template>
  <v-container fluid>
    <h2>Quiz by Code</h2>
    <v-container class="input-box">
      <v-card v-if="!hasCode">
        <v-card-text>
          <v-form ref="form" v-if="!hasCode">
            <v-row justify="center">
              <v-col cols="8" sm="4" md="2">
                <v-text-field
                  label="Code"
                  v-model="code"
                  outlined
                ></v-text-field>
                <v-btn color="primary" @click="setCode">Send</v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
      <v-card v-else>
        <v-card-title class="justify-center">
          Hold on and wait {{ timer }} to start the quiz!
        </v-card-title>
      </v-card>
    </v-container>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component
export default class CodeView extends Vue {
  hasCode: boolean = false;
  code: number | null = null;
  quiz: StatementQuiz | null = null;
  timer: string = '';

  async setCode() {
    this.hasCode = true;
    await this.getQuizByCode();
  }

  async getQuizByCode() {
    await this.$store.dispatch('loading');
    if (this.code && this.$router.currentRoute.name === 'code') {
      try {
        this.quiz = await RemoteServices.getQuizByCode(
          this.$store.getters.getCurrentCourse.courseExecutionId,
          this.code
        );

        if (!this.quiz.timeToAvailability) {
          await this.$store.dispatch('statementQuiz', this.quiz);
          await this.$router.push({ name: 'solve-quiz' });
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
    await this.$store.dispatch('clearLoading');
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

<style lang="scss" scoped>
.input-box {
  width: 40% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}
</style>
