<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-data-table
      :headers="headers"
      :items="quizAnswers.quizAnswers"
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
          <span>{{ convertToHHMMSS(quizAnswers.timeToSubmission) }}</span>
        </v-card-title>
      </template>

      <template v-slot:item.submissionLag="{ item }">
        <span
          v-bind:class="[
            new Date(item.answerDate).getTime() -
              new Date(conclusionDate).getTime() <
            0
              ? 'green'
              : 'red darken-4'
          ]"
        >
          {{
            convertToHHMMSS(
              new Date(item.answerDate).getTime() -
                new Date(conclusionDate).getTime()
            )
          }}
        </span>
      </template>

      <template v-slot:item.answers="{ item }">
        <td
          v-for="questionAnswer in item.questionAnswers"
          :key="questionAnswer.question.id"
          v-bind:class="[
            questionAnswer.answerDetails.isCorrect() ? 'green' : 'red darken-4'
          ]"
          style="border: 0"
        >
          {{ questionAnswer.answerDetails.answerRepresentation() }}
        </td>
        <template v-if="item.questionAnswers.length === 0">
          <td
            v-for="i in quizAnswers.correctSequence.length"
            :key="i"
            style="border: 0"
          >
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
              v-for="(sequence, index) in quizAnswers.correctSequence"
              :key="index"
              style="border: 0"
            >
              {{ sequence }}
            </td>
          </div>
        </tr>
      </template>
    </v-data-table>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import { QuizAnswers } from '@/models/management/QuizAnswers';

@Component
export default class ShowStudentAnswersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswers!: QuizAnswers;
  @Prop({ required: true }) readonly conclusionDate!: String;

  search: string = '';
  timeout: number | null = null;

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
      text: 'Submission Lag',
      value: 'submissionLag',
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

  convertToHHMMSS(time: number | undefined | null): string {
    return milisecondsToHHMMSS(time);
  }
}
</script>
