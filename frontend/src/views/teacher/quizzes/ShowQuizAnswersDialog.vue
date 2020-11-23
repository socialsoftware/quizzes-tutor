<template>
<div>
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

      <template v-slot:[`item.answers`]="{ item }">
        <span
          v-for="(questionAnswer, index) in item.questionAnswers"
          :key="questionAnswer.question.id"
          v-bind:class="[
            'answer',
            questionAnswer.answerDetails.isCorrect() ? 'correct' : 'incorrect'
          ]"
          @click="openAnswerDetailsDialog(item.questionAnswers, index)"
        >{{ questionAnswer.answerDetails.answerRepresentation() }}</span>
        <template v-if="item.questionAnswers.length === 0">
          <span
            v-for="i in quizAnswers.correctSequence.length"
            :key="i"
            class='answer'
          >X</span>
        </template>
      </template>

      <template v-slot:[`body.append`]>
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
<v-dialog
        v-model="dialog2"
        max-width="500px"
      >
        <v-card>
          <v-card-title>
            {{ questionDetailCurrent !== undefined ? questionAnswersDetails[questionDetailCurrent].question.title : "" }}
          </v-card-title>
          <v-card-actions>
            <v-btn
              color="primary"
              text
              @click="dialog2 = false"
            >
              Close
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

</div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Watch } from 'vue-property-decorator';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';

@Component
export default class ShowStudentAnswersDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) readonly quizAnswers!: QuizAnswers;
  @Prop({ required: true }) readonly conclusionDate!: String;

  dialog2: boolean = false;
  questionAnswersDetails?: QuestionAnswer[];
  questionDetailCurrent?: number;
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

  openAnswerDetailsDialog(questionAnswers: QuestionAnswer[], index: number){
    this.questionDetailCurrent = index;
    this.questionAnswersDetails = questionAnswers;
    this.dialog2 = true;
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

    &:hover{
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
}
</style>