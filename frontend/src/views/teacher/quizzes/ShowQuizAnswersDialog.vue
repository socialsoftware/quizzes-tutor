<template>
  <div>
    <v-dialog
      :value="dialog"
      @input="$emit('dialog', false)"
      @keydown.esc="$emit('dialog', false)"
      max-width="85%"
    >
      <v-data-table
        :headers="headers"
        :items="quizAnswers.quizAnswers"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        class="show-quiz-answer"
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

        <template v-slot:[`item.submissionLag`]="{ item }">
          <span
            v-bind:class="[
              new Date(item.answerDate).getTime() -
                new Date(conclusionDate).getTime() <
              0
                ? 'green'
                : 'red darken-4',
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

        <template v-slot:[`item.answers`]="{ item }">
          <span
            v-for="(questionAnswer, index) in item.questionAnswers"
            :key="questionAnswer.question.id"
            v-bind:class="[
              'answer',
              questionAnswer.answerDetails.isCorrect(
                questionAnswer.question.questionDetailsDto
              )
                ? 'correct'
                : 'incorrect',
            ]"
            @click="openAnswerDetailsDialog(item, index)"
            >{{
              questionAnswer.answerDetails.answerRepresentation(
                questionAnswer.question.questionDetailsDto
              )
            }}</span
          >
        </template>

        <template v-slot:[`body.append`]>
          <tr>
            <td colspan="4">Correct key:</td>
            <td>
              <span
                v-for="(sequence, index) in quizAnswers.correctSequence"
                :key="index"
                class="answer-key"
              >
                {{ sequence }}
              </span>
            </td>
          </tr>
        </template>
      </v-data-table>
    </v-dialog>
    <show-quiz-answers-details-dialog
      v-if="detailDialog"
      v-model="detailDialog"
      :quizAnswer="quizAnswerDetails"
      :questionNumber="quizAnswerDetailCurrentQuestion"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import ShowQuizAnswersDetailsDialog from '@/views/teacher/quizzes/ShowQuizAnswersDetailsDialog.vue';

@Component({
  components: {
    ShowQuizAnswersDetailsDialog,
  },
})
export default class ShowStudentAnswersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswers!: QuizAnswers;
  @Prop({ required: true }) readonly conclusionDate!: String;

  detailDialog: boolean = false;
  quizAnswerDetails?: QuizAnswer;
  quizAnswerDetailCurrentQuestion?: number;
  search: string = '';
  timeout: number | null = null;

  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '5%' },
    {
      text: 'Username',
      value: 'username',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'creationDate',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Submission Lag',
      value: 'submissionLag',
      align: 'center',
      width: '5%',
    },
    {
      text: 'Answers',
      value: 'answers',
      align: 'center',
      width: '15%',
    },
  ];

  convertToHHMMSS(time: number | undefined | null): string {
    return milisecondsToHHMMSS(time);
  }

  openAnswerDetailsDialog(quizAnswerDetails: QuizAnswer, index: number) {
    this.quizAnswerDetailCurrentQuestion = index;
    this.quizAnswerDetails = quizAnswerDetails;
    this.detailDialog = true;
  }
}
</script>

<style lang="scss">
.show-quiz-answer {
  .answer {
    padding: 2px 2px;
    margin: 0px 1px;
    text-align: center;
    color: white;
    cursor: pointer;
    min-width: 15px;
    display: inline-block;

    &:hover {
      filter: brightness(0.7);
    }

    &.correct {
      background-color: #4caf50;
      border-color: #4caf50;
    }
    &.incorrect {
      background-color: #b71c1c;
      border-color: #b71c1c;
    }
  }

  .answer-key {
    padding: 2px 2px;
    margin: 0px 1px;
    text-align: center;
    color: white;
    min-width: 15px;
    display: inline-block;
    background-color: #808080;
  }
}
</style>
