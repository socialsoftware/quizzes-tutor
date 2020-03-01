<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', $event.target)"
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
      multi-sort
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
        </v-card-title>
      </template>

      <template v-slot:item.answers="{ item }">
        <td
          v-for="questionAnswer in item.questionAnswers"
          :key="questionAnswer.question.id"
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
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import { QuizAnswer } from '@/models/management/QuizAnswer';

@Component
export default class ShowStudentAnswersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswers!: QuizAnswer[];
  @Prop({ required: true }) readonly correctSequence!: number[];

  search: string = '';

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
      value: 'startDate',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Submission Date',
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

  convertToLetter(number: number) {
    if (number === undefined) {
      return 'X';
    } else {
      return String.fromCharCode(65 + number);
    }
  }
}
</script>
