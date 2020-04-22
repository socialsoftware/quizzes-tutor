<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-data-table
      :headers="headers"
      :items="quizAnswers"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />

          <v-spacer />
          <span v-if="timeToSubmission > 0">{{ getTimeAsHHMMSS }}</span>
        </v-card-title>
      </template>

      <template v-slot:item.answers="{ item }">
        <td
          v-for="questionAnswer in item.questionAnswers"
          :key="questionAnswer.question.id"
          v-bind:class="[
            questionAnswer.option.correct ? 'green' : 'red darken-4'
          ]"
          style="border: 0"
        >
          {{ convertToLetter(questionAnswer.option.sequence) }}
        </td>
        <template v-if="item.questionAnswers.length === 0">
          <td v-for="i in correctSequence.length" :key="i" style="border: 0">
            X
          </td>
        </template>
      </template>

      <template v-slot:body.append>
        <tr>
          <td colspan="4">
            Correct key:
          </td>
          <div>
            <td></td>
            <td
              v-for="(sequence, index) in correctSequence"
              :key="index"
              style="border: 0"
            >
              {{ convertToLetter(sequence) }}
            </td>
          </div>
        </tr>
      </template>
    </v-data-table>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import { QuizAnswer } from '@/models/management/QuizAnswer';

@Component
export default class ShowStudentAnswersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswers!: QuizAnswer[];
  @Prop({ required: true }) readonly correctSequence!: number[];
  @Prop({ required: true }) readonly timeToSubmission!: number;

  secondsLeft: number = 0;
  search: string = '';
  timeout: number | null = null;

  @Watch('timeToSubmission')
  updateTimer() {
    if (this.timeToSubmission > 0) {
      this.secondsLeft = this.timeToSubmission;
      if (this.timeout) {
        clearTimeout(this.timeout);
      }
      this.countDownTimer();
    }
  }

  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '5%' },
    {
      text: 'Username',
      value: 'username',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Start Date',
      value: 'creationDate',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Answer Date',
      value: 'answerDate',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Answers',
      value: 'answers',
      align: 'center',
      width: '5%'
    }
  ];

  countDownTimer() {
    if (this.secondsLeft >= 0) {
      this.secondsLeft -= 1;
      this.timeout = setTimeout(() => {
        this.countDownTimer();
      }, 1000);
    }
  }

  get getTimeAsHHMMSS() {
    let hours = Math.floor(this.secondsLeft / 3600);
    let minutes = Math.floor((this.secondsLeft - hours * 3600) / 60);
    let seconds = this.secondsLeft - hours * 3600 - minutes * 60;

    let hoursString = hours < 10 ? '0' + hours : hours;
    let minutesString = minutes < 10 ? '0' + minutes : minutes;
    let secondsString = seconds < 10 ? '0' + seconds : seconds;

    return `${hoursString}:${minutesString}:${secondsString}`;
  }

  convertToLetter(number: number) {
    if (number === undefined) {
      return 'X';
    } else {
      return String.fromCharCode(65 + number);
    }
  }
}
</script>
