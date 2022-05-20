<template>
  <v-card class="mx-auto my-12" max-width="374">
    <v-card-title>Quiz by Code</v-card-title>
    <v-card-text>
      <v-row align="center" class="mx-0">
        <v-text-field label="Code" v-model="code" required></v-text-field>
      </v-row>
      <v-btn :disabled="code == null" color="primary" @click="setCode"
        >Send</v-btn
      >
    </v-card-text>
  </v-card>
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
