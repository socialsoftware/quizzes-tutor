<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="$emit('close-student-answers-dialog')"
    max-width="75%"
  >
    <v-data-table
      :headers="headers"
      :items="quizAnswers"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
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
        <span
          v-for="questionAnswer in item.questionAnswers"
          :key="questionAnswer"
        >
          {{ convertToLetter(questionAnswer.option.sequence) }}
        </span>
      </template>
    </v-data-table>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { QuizAnswer } from '@/models/management/QuizAnswer';

@Component
export default class ShowStudentAnswersDialog extends Vue {
  @Prop({ required: true }) readonly quizAnswers!: QuizAnswer[] | null;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
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
    return String.fromCharCode(65 + number);
  }
}
</script>
